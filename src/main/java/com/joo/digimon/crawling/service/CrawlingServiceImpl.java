package com.joo.digimon.crawling.service;

import com.joo.digimon.card.model.*;
import com.joo.digimon.card.repository.*;
import com.joo.digimon.crawling.dto.CrawlingCardDto;
import com.joo.digimon.crawling.dto.ReflectCardRequestDto;
import com.joo.digimon.crawling.dto.ReflectCardResponseDto;
import com.joo.digimon.crawling.enums.CardType;
import com.joo.digimon.crawling.enums.Color;
import com.joo.digimon.crawling.enums.Form;
import com.joo.digimon.crawling.enums.Rarity;
import com.joo.digimon.crawling.model.CrawlingCardEntity;
import com.joo.digimon.crawling.repository.CrawlingCardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CrawlingServiceImpl implements CrawlingService {

    private final CrawlingCardRepository crawlingCardRepository;
    private final CardRepository cardRepository;
    private final CardImgRepository cardImgRepository;
    private final CardCombineTypeRepository cardCombineTypeRepository;
    private final ParallelCardImgRepository parallelCardImgRepository;
    private final TypeRepository typeRepository;
    private final NoteRepository noteRepository;

    @Override
    public List<CrawlingCardDto> getUnreflectedCrawlingCardDtoList() {
        List<CrawlingCardEntity> crawlingCardEntities = crawlingCardRepository.findByCardImgEntityIsNullAndParallelCardImgEntityIsNull();
        List<CrawlingCardDto> crawlingCardDtoList = new ArrayList<>();
        for (CrawlingCardEntity crawlingCardEntity : crawlingCardEntities) {
            crawlingCardDtoList.add(new CrawlingCardDto(crawlingCardEntity));
        }
        return crawlingCardDtoList;
    }

    @Override
    @Transactional
    public List<ReflectCardResponseDto> saveCardByReflectCardRequestList(List<ReflectCardRequestDto> reflectCardRequestDtoList) {
        List<ReflectCardResponseDto> reflectCardResponseDtoList = new ArrayList<>();
        for (ReflectCardRequestDto reflectCardRequestDto : reflectCardRequestDtoList) {
            try {
                reflectCardResponseDtoList.add(new ReflectCardResponseDto(reflectCardRequestDto.getId(), saveCardByReflectCardRequest(reflectCardRequestDto)));
            } catch (Exception e) {
                reflectCardResponseDtoList.add(new ReflectCardResponseDto(reflectCardRequestDto.getId(), false));
            }

        }
        return reflectCardResponseDtoList;
    }


    @Transactional
    public boolean saveCardByReflectCardRequest(ReflectCardRequestDto reflectCardRequestDto) {
        CrawlingCardEntity crawlingCardEntity = crawlingCardRepository.findById(reflectCardRequestDto.getId()).orElseThrow();


        CardEntity cardEntity = getCardEntityOrInsert(reflectCardRequestDto);
        NoteEntity noteEntity = noteRepository.findByName(reflectCardRequestDto.getNote()).orElseGet(
                () -> noteRepository.save(NoteEntity.builder()
                        .name(reflectCardRequestDto.getNote())
                        .build())
        );

        try {
            if (reflectCardRequestDto.getIsParallel()) {
                parallelCardImgRepository.save(
                        ParallelCardImgEntity.builder()
                                .noteEntity(noteEntity)
                                .crawlingCardEntity(crawlingCardEntity)
                                .cardEntity(cardEntity)
                                .originUrl(reflectCardRequestDto.getOriginUrl())
                                .build()
                );
                return true;
            }
            cardImgRepository.save(
                    CardImgEntity.builder()
                            .noteEntity(noteEntity)
                            .crawlingCardEntity(crawlingCardEntity)
                            .cardEntity(cardEntity)
                            .originUrl(reflectCardRequestDto.getOriginUrl())
                            .build()
            );
        } catch (DataIntegrityViolationException e) {
            return false;
        }

        return true;


    }

    private CardEntity getCardEntityOrInsert(ReflectCardRequestDto reflectCardRequestDto) {
        return cardRepository.findByCardNo(reflectCardRequestDto.getCardNo()).orElseGet(
                () -> {
                    CardEntity save = cardRepository.save(
                            CardEntity.builder()
                                    .cardNo(reflectCardRequestDto.getCardNo())
                                    .cardName(reflectCardRequestDto.getCardName())
                                    .attribute(reflectCardRequestDto.getAttribute())
                                    .dP(reflectCardRequestDto.getDP())
                                    .playCost(reflectCardRequestDto.getPlayCost())
                                    .digivolveCondition1(reflectCardRequestDto.getDigivolveCondition1())
                                    .digivolveCondition2(reflectCardRequestDto.getDigivolveCondition2())
                                    .digivolveCost1(reflectCardRequestDto.getDigivolveCost1())
                                    .digivolveCost2(reflectCardRequestDto.getDigivolveCost2())
                                    .lv(reflectCardRequestDto.getLv())
                                    .effect(reflectCardRequestDto.getEffect())
                                    .sourceEffect(reflectCardRequestDto.getSourceEffect())
                                    .cardType(CardType.findByKor(reflectCardRequestDto.getCardType()))
                                    .form(Form.findByKor(reflectCardRequestDto.getForm()))
                                    .rarity(Rarity.valueOf(reflectCardRequestDto.getRarity()))
                                    .color1(Color.getColorByString(reflectCardRequestDto.getColor1()))
                                    .color2(Color.getColorByString(reflectCardRequestDto.getColor2()))
                                    .build());
                    for (String type : reflectCardRequestDto.getType()) {
                        TypeEntity typeEntity = typeRepository.findByName(type)
                                .orElseGet(() ->
                                        typeRepository.save(TypeEntity.builder()
                                                .name(type)
                                                .build())
                                );
                        cardCombineTypeRepository.save(
                                CardCombineTypeEntity.builder()
                                        .cardEntity(save)
                                        .typeEntity(typeEntity)
                                        .build()
                        );
                    }
                    return save;
                }
        );
    }

    @Override
    @Transactional
    public int crawlAndSaveByUrl(String url) throws IOException {
        List<CrawlingCardEntity> crawlingCardEntities = crawlUrlAndBuildEntityList(url);
        int cnt = 0;
        for (CrawlingCardEntity crawlingCardEntity : crawlingCardEntities) {
            if (crawlingCardRepository.findByImgUrl(crawlingCardEntity.getImgUrl()).isEmpty()) {

                crawlingCardRepository.save(crawlingCardEntity);
                cnt++;
            }
        }
        return cnt;

    }

    @Override
    public List<CrawlingCardEntity> crawlUrlAndBuildEntityList(String url) throws IOException {
        List<Document> documentListByFirstPageUrl = getDocumentListByFirstPageUrl(url);

        List<Element> cardElement = new ArrayList<>();
        for (Document document : documentListByFirstPageUrl) {
            cardElement.addAll(getCardElementsByDocument(document));
        }
        List<CrawlingCardDto> crawlingCardDtoList = new ArrayList<>();
        for (Element element : cardElement) {
            crawlingCardDtoList.add(crawlingCardByElement(element));
        }
        List<CrawlingCardEntity> crawlingCardEntities = new ArrayList<>();

        for (CrawlingCardDto crawlingCardDto : crawlingCardDtoList) {
            crawlingCardEntities.add(
                    new CrawlingCardEntity(crawlingCardDto)
            );
        }


        return crawlingCardEntities;
    }

    @Override
    public List<Document> getDocumentListByFirstPageUrl(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        List<Document> documentList = new ArrayList<>();
        documentList.add(doc);
        try {
            Elements pages = doc.selectFirst(".paging").select("ul>li>a");
            for (Element page : pages) {
                if (page.text().equals("Next"))
                    break;
                documentList.add(Jsoup.connect(page.attr("href")).get());
            }
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("paging 클래스 태그를 찾을 수 없음");
        }


        return documentList;
    }

    @Override
    public List<Element> getCardElementsByDocument(Document document) {
        return document.select(".cardlistCol .popup");
    }

    @Override
    public CrawlingCardDto crawlingCardByElement(Element element) {
        CrawlingCardDto crawlingCardDto = new CrawlingCardDto();

        crawlingCardDto.setIsParallel(!element.select(".cardtype cardParallel").isEmpty());
        extractCardColor(crawlingCardDto, element);
        extractCardInfoHead(element, crawlingCardDto);
        extractCardInfoBody(element, crawlingCardDto);
        extractCardInfoBottom(element, crawlingCardDto);

        return crawlingCardDto;
    }

    private void extractCardInfoBottom(Element element, CrawlingCardDto crawlingCardDto) {
        crawlingCardDto.setEffect(changeJapanMiddlePoint(parseElementToPlainText(element.select(".cardinfo_bottom dl:contains(상단 텍스트) dd"))));
        crawlingCardDto.setSourceEffect(changeJapanMiddlePoint(parseElementToPlainText(element.select(".cardinfo_bottom dl:contains(하단 텍스트) dd"))));
        crawlingCardDto.setNote(changeJapanMiddlePoint(element.select(".cardinfo_bottom dl:contains(입수 정보) dd").text()));
    }

    private String parseElementToPlainText(Elements select) {
        return select.html().replace("<br>\n", "");
    }

    private void extractCardInfoBody(Element element, CrawlingCardDto crawlingCardDto) {
        Element lvElement = element.selectFirst(".cardlv");
        if (lvElement != null) {
            crawlingCardDto.setLv(lvElement.text());
        }


        crawlingCardDto.setCardName(changeJapanMiddlePoint(element.selectFirst(".card_name").text()));

        crawlingCardDto.setImgUrl(element.select(".card_img>img").attr("src"));
        crawlingCardDto.setForm(element.select(".cardinfo_top_body dl:contains(형태) dd").text());
        crawlingCardDto.setAttribute(element.select(".cardinfo_top_body dl:contains(속성) dd").text());
        crawlingCardDto.setType(element.select(".cardinfo_top_body dl:contains(유형) dd").text());
        crawlingCardDto.setDP(element.select(".cardinfo_top_body dl:contains(DP) dd").text());
        crawlingCardDto.setPlayCost(element.select(".cardinfo_top_body dl:contains(등장 코스트) dd").text());
        crawlingCardDto.setDigivolveCost1(element.select(".cardinfo_top_body dl:contains(진화 코스트 1) dd").text());
        crawlingCardDto.setDigivolveCost2(element.select(".cardinfo_top_body dl:contains(진화 코스트 2) dd").text());
    }

    private void extractCardInfoHead(Element element, CrawlingCardDto crawlingCardDto) {
        crawlingCardDto.setCardNo(element.selectFirst(".cardno").text());
        crawlingCardDto.setRarity(element.select(".cardinfo_head>li").get(1).text());
        crawlingCardDto.setCardType(element.selectFirst(".cardtype").text());
    }

    private void extractCardColor(CrawlingCardDto crawlingCardDto, Element element) {
        String classAttribute = element.selectFirst(".card_detail").className();
        Pattern pattern = Pattern.compile("card_detail_(\\w+)");
        Matcher matcher = pattern.matcher(classAttribute);

        if (matcher.find()) {
            String[] colorText = matcher.group(1).split("_");
            crawlingCardDto.setColor1(colorText[0]);
            if (colorText.length == 2) {
                crawlingCardDto.setColor2(colorText[1]);
            }
        }
    }

    private String changeJapanMiddlePoint(String text) {
        char[] textArray = text.toCharArray();
        for (int i = 0; i < textArray.length; i++) {
            if (textArray[i] == 12539) {
                textArray[i] = '·';
            }
        }
        return new String(textArray);
    }
}
