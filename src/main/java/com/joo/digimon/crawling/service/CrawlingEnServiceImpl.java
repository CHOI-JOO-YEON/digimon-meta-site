package com.joo.digimon.crawling.service;

import com.joo.digimon.card.model.*;
import com.joo.digimon.card.repository.CardImgRepository;
import com.joo.digimon.card.repository.CardRepository;
import com.joo.digimon.card.repository.NoteRepository;
import com.joo.digimon.crawling.dto.CrawlingCardDto;
import com.joo.digimon.crawling.dto.CrawlingResultDto;
import com.joo.digimon.crawling.dto.ReflectCardRequestDto;
import com.joo.digimon.crawling.dto.UpdateCrawlingRequestDto;
import com.joo.digimon.crawling.enums.CardOrigin;
import com.joo.digimon.crawling.model.CrawlingCardEntity;
import com.joo.digimon.crawling.repository.CrawlingCardRepository;
import com.joo.digimon.exception.model.CardParseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class CrawlingEnServiceImpl implements CrawlingEnService {
    private final CardImgRepository cardImgRepository;
    private final CrawlingCardRepository crawlingCardRepository;
    private final CardRepository cardRepository;
    private final NoteRepository noteRepository;
    private final CardParseService cardParseService;
    @Transactional
    @Override
    public CrawlingResultDto updateCrawlingEntityAndSaveCard(List<UpdateCrawlingRequestDto> updateCrawlingRequestDtoList) {
        CrawlingResultDto crawlingResultDto = new CrawlingResultDto();
        for (UpdateCrawlingRequestDto updateCrawlingRequestDto : updateCrawlingRequestDtoList) {
            CrawlingCardEntity crawlingCard = crawlingCardRepository.findById(updateCrawlingRequestDto.getId()).orElseThrow();

            if (crawlingCardRepository.findById(updateCrawlingRequestDto.getId()).orElseThrow().getIsReflect()) {
                CrawlingCardDto crawlingCardDto = new CrawlingCardDto(crawlingCard);
                crawlingCardDto.setErrorMessage("IS_REFLECTED");
                crawlingResultDto.addFailedCrawling(crawlingCardDto);
                continue;
            }

            CrawlingCardEntity crawlingCardEntity = updateCrawlingEntity(updateCrawlingRequestDto);
            try {
                saveCardByReflectCardRequest(cardParseService.crawlingCardParseEn(crawlingCardEntity));
                crawlingResultDto.successCountIncrease();
                crawlingCardEntity.setIsReflect(true);
            } catch (CardParseException e) {
                crawlingCardEntity.updateErrorMessage(e.getMessage());
                crawlingResultDto.addFailedCrawling(new CrawlingCardDto(crawlingCardEntity));
            } catch (Exception e) {
                log.error("{} 에서 {} 발생 {}", crawlingCardEntity, e.getMessage(), e);
            }
        }

        return crawlingResultDto;
    }
    @Transactional
    public CrawlingCardEntity updateCrawlingEntity(UpdateCrawlingRequestDto updateCrawlingRequestDto) {
        CrawlingCardEntity crawlingCardEntity = crawlingCardRepository.findById(updateCrawlingRequestDto.getId()).orElseThrow();
        Optional.ofNullable(updateCrawlingRequestDto.getCardNo()).ifPresent(crawlingCardEntity::setCardNo);
        Optional.ofNullable(updateCrawlingRequestDto.getRarity()).ifPresent(crawlingCardEntity::setRarity);
        Optional.ofNullable(updateCrawlingRequestDto.getCardType()).ifPresent(crawlingCardEntity::setCardType);
        Optional.ofNullable(updateCrawlingRequestDto.getLv()).ifPresent(crawlingCardEntity::setLv);
        Optional.ofNullable(updateCrawlingRequestDto.getIsParallel()).ifPresent(crawlingCardEntity::setIsParallel);
        Optional.ofNullable(updateCrawlingRequestDto.getCardName()).ifPresent(crawlingCardEntity::setCardName);
        Optional.ofNullable(updateCrawlingRequestDto.getForm()).ifPresent(crawlingCardEntity::setForm);
        Optional.ofNullable(updateCrawlingRequestDto.getAttribute()).ifPresent(crawlingCardEntity::setAttribute);
        Optional.ofNullable(updateCrawlingRequestDto.getType()).ifPresent(crawlingCardEntity::setType);
        Optional.ofNullable(updateCrawlingRequestDto.getDP()).ifPresent(crawlingCardEntity::setDP);
        Optional.ofNullable(updateCrawlingRequestDto.getPlayCost()).ifPresent(crawlingCardEntity::setPlayCost);
        Optional.ofNullable(updateCrawlingRequestDto.getDigivolveCost1()).ifPresent(crawlingCardEntity::setDigivolveCost1);
        Optional.ofNullable(updateCrawlingRequestDto.getDigivolveCost2()).ifPresent(crawlingCardEntity::setDigivolveCost2);
        Optional.ofNullable(updateCrawlingRequestDto.getEffect()).ifPresent(crawlingCardEntity::setEffect);
        Optional.ofNullable(updateCrawlingRequestDto.getSourceEffect()).ifPresent(crawlingCardEntity::setSourceEffect);
        Optional.ofNullable(updateCrawlingRequestDto.getNote()).ifPresent(crawlingCardEntity::setNote);
        Optional.ofNullable(updateCrawlingRequestDto.getColor1()).ifPresent(crawlingCardEntity::setColor1);
        Optional.ofNullable(updateCrawlingRequestDto.getColor2()).ifPresent(crawlingCardEntity::setColor2);
        Optional.ofNullable(updateCrawlingRequestDto.getImgUrl()).ifPresent(crawlingCardEntity::setImgUrl);
        return crawlingCardEntity;
    }

    @Override
    public CrawlingResultDto crawlAndSaveByUrl(String url) throws IOException {
        List<CrawlingCardEntity> crawlingCardEntities = crawlUrlAndBuildEntityList(url);
        CrawlingResultDto crawlingResultDto = new CrawlingResultDto();

        for (CrawlingCardEntity crawlingCardEntity : crawlingCardEntities) {
            if (cardImgRepository.findByCrawlingCardEntity(crawlingCardEntity).isPresent()) {
                crawlingResultDto.alreadyReflectCountIncrease();
                continue;
            }
            try {
                saveCardByReflectCardRequest(cardParseService.crawlingCardParseEn(crawlingCardEntity));
                crawlingResultDto.successCountIncrease();
                crawlingCardEntity.setIsReflect(true);
            } catch (CardParseException e) {
                crawlingCardEntity.updateErrorMessage(e.getMessage());
                crawlingResultDto.addFailedCrawling(new CrawlingCardDto(crawlingCardEntity));
            } catch (Exception e) {
                log.error("{} 에서 {} 발생 {}", crawlingCardEntity, e.getMessage(), e);
            }
        }

        return crawlingResultDto;
    }

    @Override
    public CrawlingResultDto crawlAndSaveByUrl(String url, String note) throws IOException {
        List<CrawlingCardEntity> crawlingCardEntities = crawlUrlAndBuildEntityList(url, note);
        CrawlingResultDto crawlingResultDto = new CrawlingResultDto();

        for (CrawlingCardEntity crawlingCardEntity : crawlingCardEntities) {
            if (cardImgRepository.findByCrawlingCardEntity(crawlingCardEntity).isPresent()) {
                crawlingResultDto.alreadyReflectCountIncrease();
                continue;
            }
            try {
                saveCardByReflectCardRequest(cardParseService.crawlingCardParseEn(crawlingCardEntity));
                crawlingResultDto.successCountIncrease();
                crawlingCardEntity.setIsReflect(true);
            } catch (CardParseException e) {
                crawlingCardEntity.updateErrorMessage(e.getMessage());
                crawlingResultDto.addFailedCrawling(new CrawlingCardDto(crawlingCardEntity));
            } catch (Exception e) {
                log.error("{} 에서 {} 발생 {}", crawlingCardEntity, e.getMessage(), e);
            }
        }

        return crawlingResultDto;
    }

    public List<Element> getCardElementsByDocument(Document document) {
        return document.select(".cardlistCol .popup");
    }

    public List<CrawlingCardEntity> crawlUrlAndBuildEntityList(String url) throws IOException {
        List<Document> documentListByFirstPageUrl = getDocumentListByFirstPageUrl(url);

        List<Element> cardElement = new ArrayList<>();
        for (Document document : documentListByFirstPageUrl) {
            cardElement.addAll(getCardElementsByDocument(document));
        }
        List<CrawlingCardDto> crawlingCardDtoList = new ArrayList<>();
        for (Element element : cardElement) {
            CrawlingCardDto crawlingCardDto = crawlingCardByElement(element);
            if (!crawlingCardDto.getIsParallel()) {
                crawlingCardDtoList.add(crawlingCardDto);
            }

        }
        List<CrawlingCardEntity> crawlingCardEntities = new ArrayList<>();

        for (CrawlingCardDto crawlingCardDto : crawlingCardDtoList) {
            crawlingCardEntities.add(
                    createOrFindCrawlingCardEntity(crawlingCardDto)
            );
        }


        return crawlingCardEntities;
    }

    public List<CrawlingCardEntity> crawlUrlAndBuildEntityList(String url, String note) throws IOException {
        List<Document> documentListByFirstPageUrl = getDocumentListByFirstPageUrl(url);

        List<Element> cardElement = new ArrayList<>();
        for (Document document : documentListByFirstPageUrl) {
            cardElement.addAll(getCardElementsByDocument(document));
        }
        List<CrawlingCardDto> crawlingCardDtoList = new ArrayList<>();
        for (Element element : cardElement) {
            CrawlingCardDto crawlingCardDto = crawlingCardByElement(element);
            if (!crawlingCardDto.getIsParallel()&&crawlingCardDto.getNote().equals(note)) {
                crawlingCardDtoList.add(crawlingCardDto);
            }

        }
        List<CrawlingCardEntity> crawlingCardEntities = new ArrayList<>();

        for (CrawlingCardDto crawlingCardDto : crawlingCardDtoList) {
            crawlingCardEntities.add(
                    createOrFindCrawlingCardEntity(crawlingCardDto)
            );
        }


        return crawlingCardEntities;
    }

    private CrawlingCardEntity createOrFindCrawlingCardEntity(CrawlingCardDto crawlingCardDto) {
        return crawlingCardRepository.findByImgUrl(crawlingCardDto.getImgUrl()).orElseGet(() -> crawlingCardRepository.save(new CrawlingCardEntity(crawlingCardDto, true)));
    }

    @Transactional
    public boolean saveCardByReflectCardRequest(ReflectCardRequestDto reflectCardRequestDto) throws CardParseException {
        CrawlingCardEntity crawlingCardEntity = crawlingCardRepository.findById(reflectCardRequestDto.getId()).orElseThrow();


        CardEntity cardEntity = getCardEntityOrInsert(reflectCardRequestDto);
        NoteEntity noteEntity = noteRepository.findByName(reflectCardRequestDto.getNote()).orElseGet(
                () -> noteRepository.save(NoteEntity.builder()
                        .name(reflectCardRequestDto.getNote())
                        .cardOrigin(CardOrigin.ENGLISH)
                        .build())
        );


        cardImgRepository.save(
                CardImgEntity.builder()
                        .isParallel(reflectCardRequestDto.getIsParallel())
                        .noteEntity(noteEntity)
                        .crawlingCardEntity(crawlingCardEntity)
                        .cardEntity(cardEntity)
                        .originUrl(reflectCardRequestDto.getOriginUrl())
                        .isEnCard(true)
                        .build()
        );


        return true;


    }

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

    public CrawlingCardDto crawlingCardByElement(Element element) {
        CrawlingCardDto crawlingCardDto = new CrawlingCardDto();

        crawlingCardDto.setIsParallel(!element.select(".cardParallel").isEmpty());
        extractCardColor(crawlingCardDto, element);
        extractCardInfoHead(element, crawlingCardDto);
        extractCardInfoTop(element, crawlingCardDto);
        extractCardInfoBottom(element, crawlingCardDto);

        return crawlingCardDto;
    }

    private void extractCardInfoHead(Element element, CrawlingCardDto crawlingCardDto) {
        Element lvElement = element.selectFirst(".cardlv");
        if (lvElement != null) {
            crawlingCardDto.setLv(lvElement.text());
        }

        crawlingCardDto.setCardNo(element.selectFirst(".cardno").text());
        crawlingCardDto.setRarity(element.select(".cardinfo_head>li").get(1).text());
        crawlingCardDto.setCardType(element.selectFirst(".cardtype").text());
    }

    private void extractCardInfoBottom(Element element, CrawlingCardDto crawlingCardDto) {
        crawlingCardDto.setEffect(parseElementToPlainText(element.select(".cardinfo_bottom dl dt:matchesOwn(^Effect$) + dd")));
        crawlingCardDto.setSourceEffect(parseElementToPlainText(element.select(".cardinfo_bottom dl:contains(Inherited Effect) dd")));
        crawlingCardDto.setNote(element.select(".cardinfo_bottom dl:contains(Notes) dd").text());
    }

    private String parseElementToPlainText(Elements select) {
        return select.html().replace("<br>\n", "");
    }

    private void extractCardInfoTop(Element element, CrawlingCardDto crawlingCardDto) {


        crawlingCardDto.setCardName(element.selectFirst(".card_name").text());

        crawlingCardDto.setImgUrl(element.select(".card_img>img").attr("src"));
        crawlingCardDto.setForm(element.select(".cardinfo_top_body dl:contains(Form) dd").text());
        crawlingCardDto.setAttribute(element.select(".cardinfo_top_body dl:contains(Attribute) dd").text());
        crawlingCardDto.setType(element.select(".cardinfo_top_body dl:contains(Type) dd").text());
        crawlingCardDto.setDP(element.select(".cardinfo_top_body dl:contains(DP) dd").text());
        crawlingCardDto.setPlayCost(element.select(".cardinfo_top_body dl:contains(Play Cost) dd").text());
        crawlingCardDto.setDigivolveCost1(element.select(".cardinfo_top_body dl:contains(Digivolve Cost 1) dd").text());
        crawlingCardDto.setDigivolveCost2(element.select(".cardinfo_top_body dl:contains(Digivolve Cost 2) dd").text());
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

    private CardEntity getCardEntityOrInsert(ReflectCardRequestDto reflectCardRequestDto) {
        return cardRepository.findByCardNo(reflectCardRequestDto.getCardNo()).orElseGet(
                () -> {
                    CardEntity save = cardRepository.save(
                            CardEntity.builder()
                                    .sortString(generateSortString(reflectCardRequestDto.getCardNo()))
                                    .cardNo(reflectCardRequestDto.getCardNo())
                                    .cardName(reflectCardRequestDto.getCardName())
                                    .attribute(reflectCardRequestDto.getAttribute())
                                    .dp(reflectCardRequestDto.getDp())
                                    .playCost(reflectCardRequestDto.getPlayCost())
                                    .digivolveCondition1(reflectCardRequestDto.getDigivolveCondition1())
                                    .digivolveCondition2(reflectCardRequestDto.getDigivolveCondition2())
                                    .digivolveCost1(reflectCardRequestDto.getDigivolveCost1())
                                    .digivolveCost2(reflectCardRequestDto.getDigivolveCost2())
                                    .lv(reflectCardRequestDto.getLv())
                                    .effect(reflectCardRequestDto.getEffect())
                                    .sourceEffect(reflectCardRequestDto.getSourceEffect())
                                    .cardType(reflectCardRequestDto.getCardType())
                                    .form(reflectCardRequestDto.getForm())
                                    .rarity(reflectCardRequestDto.getRarity())
                                    .color1(reflectCardRequestDto.getColor1())
                                    .color2(reflectCardRequestDto.getColor2())
                                    .isOnlyEnCard(true)
                                    .releaseDate(LocalDate.of(9999, 12, 31))
                                    .build());

                    return save;
                }
        );
    }

    private String generateSortString(String cardNo) {
        StringBuilder stringBuilder = new StringBuilder();
        if (cardNo.startsWith("BT")) {
            stringBuilder.append("A");
        } else if (cardNo.startsWith("ST")) {
            stringBuilder.append("B");
        } else if (cardNo.startsWith("EX")) {
            stringBuilder.append("C");
        } else if (cardNo.startsWith("RB")) {
            stringBuilder.append("D");
        } else if (cardNo.startsWith("P")) {
            stringBuilder.append("Z");
            String[] parts = cardNo.split("-");
            String firstNumberPart = String.format("%03d", Integer.parseInt(parts[1].replaceAll("\\D", "")));
            stringBuilder.append(firstNumberPart);
            return stringBuilder.toString();
        } else {
            stringBuilder.append("E");
        }
        String[] parts = cardNo.split("-");

        String firstNumberPart = String.format("%03d", Integer.parseInt(parts[0].replaceAll("\\D", "")));
        stringBuilder.append(firstNumberPart);

        if (parts.length > 1) {
            String secondNumberPart = String.format("%03d", Integer.parseInt(parts[1]));
            stringBuilder.append(secondNumberPart);
        }

        return stringBuilder.toString();
    }
}
