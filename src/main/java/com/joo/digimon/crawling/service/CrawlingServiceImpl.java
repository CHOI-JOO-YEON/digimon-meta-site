package com.joo.digimon.crawling.service;

import com.joo.digimon.card.model.*;
import com.joo.digimon.card.repository.*;
import com.joo.digimon.crawling.dto.*;
import com.joo.digimon.crawling.model.CrawlingCardEntity;
import com.joo.digimon.crawling.model.DeletedEnCardImg;
import com.joo.digimon.crawling.repository.CrawlingCardRepository;
import com.joo.digimon.crawling.repository.DeletedEnCardImgRepository;
import com.joo.digimon.global.exception.model.CardImageException;
import com.joo.digimon.global.exception.model.CardParseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
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
public class CrawlingServiceImpl implements CrawlingService {

    private final CrawlingCardRepository crawlingCardRepository;
    private final CardRepository cardRepository;
    private final CardImgRepository cardImgRepository;
    private final CardCombineTypeRepository cardCombineTypeRepository;
    private final TypeRepository typeRepository;
    private final NoteRepository noteRepository;
    private final CardParseService cardParseService;
    private final DeletedEnCardImgRepository deletedEnCardImgRepository;
    private final EnglishCardRepository englishCardRepository;

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
                saveCardByReflectCardRequest(cardParseService.crawlingCardParse(crawlingCardEntity), crawlingCardEntity.getLocale());

                crawlingResultDto.successCountIncrease();
                crawlingCardEntity.setIsReflect(true);
            } catch (CardParseException | CardImageException e) {
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
        Optional.ofNullable(updateCrawlingRequestDto.getColor3()).ifPresent(crawlingCardEntity::setColor3);
        Optional.ofNullable(updateCrawlingRequestDto.getImgUrl()).ifPresent(crawlingCardEntity::setImgUrl);
        return crawlingCardEntity;
    }

    @Override
    public List<CrawlingCardDto> getUnreflectedCrawlingCardDtoList(Integer size) {
        Pageable pageable = PageRequest.of(0, size);
        List<CrawlingCardEntity> crawlingCardEntities = crawlingCardRepository.findByCardImgEntityIsNullAndParallelCardImgEntityIsNull(pageable);
        List<CrawlingCardDto> crawlingCardDtoList = new ArrayList<>();
        for (CrawlingCardEntity crawlingCardEntity : crawlingCardEntities) {
            crawlingCardDtoList.add(new CrawlingCardDto(crawlingCardEntity));
        }
        return crawlingCardDtoList;
    }

    @Override
    @Transactional
    public List<ReflectCardResponseDto> saveCardByReflectCardRequestList(List<ReflectCardRequestDto> reflectCardRequestDtoList, String locale) {
        List<ReflectCardResponseDto> reflectCardResponseDtoList = new ArrayList<>();
        for (ReflectCardRequestDto reflectCardRequestDto : reflectCardRequestDtoList) {
            try {
                saveCardByReflectCardRequest(reflectCardRequestDto, locale);
                reflectCardResponseDtoList.add(new ReflectCardResponseDto(reflectCardRequestDto.getId(), true));
            } catch (Exception e) {
                reflectCardResponseDtoList.add(new ReflectCardResponseDto(reflectCardRequestDto.getId(), false));
            }

        }
        return reflectCardResponseDtoList;
    }


    @Transactional
    public void saveCardByReflectCardRequest(ReflectCardRequestDto reflectCardRequestDto, String locale) throws CardParseException, CardImageException {
        CrawlingCardEntity crawlingCardEntity = crawlingCardRepository.findById(reflectCardRequestDto.getId()).orElseThrow();


        CardEntity cardEntity;

        if (locale.equals("ENG")) {
            cardEntity = getEnglishCardEntityOrInsert(reflectCardRequestDto);
        } else {
            cardEntity = getCardEntityOrInsert(reflectCardRequestDto);
        }

        NoteEntity noteEntity = noteRepository.findByName(reflectCardRequestDto.getNote()).orElseGet(() -> noteRepository.save(NoteEntity.builder().name(reflectCardRequestDto.getNote()).build()));

        if (Boolean.FALSE.equals(reflectCardRequestDto.getIsParallel())) {
            List<CardImgEntity> cardImgEntityList = cardImgRepository.findByCardEntity(cardEntity);
            if (cardImgEntityList.size() > 1) {
                throw new CardImageException("Duplicate non-parallel card image");
            }
            if (cardImgEntityList.size() == 1) {
                CardImgEntity cardImgEntity = cardImgEntityList.getFirst();

                if (!Boolean.TRUE.equals(cardImgEntity.getIsEnCard())) {
                    throw new CardImageException("Non-parallel cards already reflected");
                }
                deletedEnCardImgRepository.save(DeletedEnCardImg.builder().cardEntity(cardEntity).crawlingCardEntity(cardImgEntity.getCrawlingCardEntity()).uploadUrl(cardImgEntity.getUploadUrl()).smallImgUrl(cardImgEntity.getSmallImgUrl()).originUrl(cardImgEntity.getOriginUrl()).noteEntity(cardImgEntity.getNoteEntity()).build());
                cardImgRepository.save(CardImgEntity.builder().id(cardImgEntity.getId()).isParallel(reflectCardRequestDto.getIsParallel()).noteEntity(noteEntity).crawlingCardEntity(crawlingCardEntity).cardEntity(cardEntity).originUrl(reflectCardRequestDto.getOriginUrl()).build());
                return;
            }
        }
        cardImgRepository.save(
                CardImgEntity.builder()
                        .isParallel(reflectCardRequestDto.getIsParallel())
                        .noteEntity(noteEntity)
                        .crawlingCardEntity(crawlingCardEntity)
                        .cardEntity(cardEntity)
                        .originUrl(reflectCardRequestDto.getOriginUrl())
                        .isEnCard(locale.equals("ENG"))
                        .build());
    }

    private CardEntity getCardEntityOrInsert(ReflectCardRequestDto reflectCardRequestDto) {
        Optional<CardEntity> cardEntity = cardRepository.findByCardNo(reflectCardRequestDto.getCardNo());
        if (cardEntity.isPresent()) {
            CardEntity card = cardEntity.get();
            if (Boolean.TRUE.equals(card.getIsOnlyEnCard())) {
                CardEntity save = cardRepository.save(CardEntity.builder().id(card.getId())
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
                        .color3(reflectCardRequestDto.getColor3())
                        .isOnlyEnCard(false).build());


                for (String type : reflectCardRequestDto.getTypes()) {
                    TypeEntity typeEntity = typeRepository.findByName(type).orElseGet(() -> typeRepository.save(TypeEntity.builder().name(type).build()));
                    cardCombineTypeRepository.save(CardCombineTypeEntity.builder().cardEntity(save).typeEntity(typeEntity).build());
                }
                return save;

            }
            return cardEntity.get();
        }


        CardEntity save = cardRepository.save(CardEntity.builder()
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
                .color3(reflectCardRequestDto.getColor3())
                .build());


        for (String type : reflectCardRequestDto.getTypes()) {
            TypeEntity typeEntity = typeRepository.findByName(type).orElseGet(() -> typeRepository.save(TypeEntity.builder().name(type).build()));
            cardCombineTypeRepository.save(CardCombineTypeEntity.builder().cardEntity(save).typeEntity(typeEntity).build());
        }
        return save;

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

        // '-'를 기준으로 문자열을 분리
        String[] parts = cardNo.split("-");

        // 첫 번째 숫자 부분을 처리 (3자리로 맞춤)
        String firstNumberPart = String.format("%03d", Integer.parseInt(parts[0].replaceAll("\\D", "")));
        stringBuilder.append(firstNumberPart);

        // 두 번째 숫자 부분을 처리 (3자리로 맞춤)
        if (parts.length > 1) {
            String secondNumberPart = String.format("%03d", Integer.parseInt(parts[1]));
            stringBuilder.append(secondNumberPart);
        }

        return stringBuilder.toString();
    }


    @Override
    public CrawlingResultDto crawlAndSaveByUrl(String url, String locale, @Nullable String note) throws IOException {
        List<CrawlingCardEntity> crawlingCardEntities = crawlUrlAndBuildEntityList(url, locale, note);
        CrawlingResultDto crawlingResultDto = new CrawlingResultDto();

        for (CrawlingCardEntity crawlingCardEntity : crawlingCardEntities) {
            if (cardImgRepository.findByCrawlingCardEntity(crawlingCardEntity).isPresent()) {
                crawlingResultDto.alreadyReflectCountIncrease();
                continue;
            }
            try {
                saveCardByReflectCardRequest(cardParseService.crawlingCardParse(crawlingCardEntity), crawlingCardEntity.getLocale());
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

    public List<CrawlingCardEntity> crawlUrlAndBuildEntityList(String url, String locale, @Nullable String note) throws IOException {
        List<Document> documentListByFirstPageUrl = getDocumentListByFirstPageUrl(url);

        List<Element> cardElement = new ArrayList<>();
        for (Document document : documentListByFirstPageUrl) {
            cardElement.addAll(getCardElementsByDocument(document));
        }
        List<CrawlingCardDto> crawlingCardDtoList = new ArrayList<>();
        for (Element element : cardElement) {

            CrawlingCardDto crawlingCardDto = crawlingCardByElement(element, locale);


            if ((note == null || crawlingCardDto.getNote().equals(note))
                    && (!locale.equals("ENG") || !crawlingCardDto.getIsParallel())) {

                crawlingCardDtoList.add(crawlingCardDto);
            }
        }
        List<CrawlingCardEntity> crawlingCardEntities = new ArrayList<>();

        for (CrawlingCardDto crawlingCardDto : crawlingCardDtoList) {
            crawlingCardEntities.add(createOrFindCrawlingCardEntity(crawlingCardDto));
        }


        return crawlingCardEntities;
    }

    private CrawlingCardEntity createOrFindCrawlingCardEntity(CrawlingCardDto crawlingCardDto) {
        return crawlingCardRepository.findByImgUrl(crawlingCardDto.getImgUrl()).orElseGet(() -> crawlingCardRepository.save(new CrawlingCardEntity(crawlingCardDto)));
    }

    @Override
    public List<Document> getDocumentListByFirstPageUrl(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        List<Document> documentList = new ArrayList<>();
        documentList.add(doc);
        try {
            Elements pages = doc.selectFirst(".paging").select("ul>li>a");
            for (Element page : pages) {
                if (page.text().equals("Next")) break;
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
    public CrawlingCardDto crawlingCardByElement(Element element, String locale) {
        CrawlingCardDto crawlingCardDto = new CrawlingCardDto();

        crawlingCardDto.setLocale(locale);
        crawlingCardDto.setIsParallel(!element.select(".cardParallel").isEmpty());
        extractCardColor(crawlingCardDto, element);
        extractCardInfoHead(element, crawlingCardDto);


        if (locale.equals("KOR")) {
            extractCardInfoBottom(element, crawlingCardDto);
            extractCardInfoBody(element, crawlingCardDto);
        } else if (locale.equals("ENG")) {
            extractEngCardInfoBottom(element, crawlingCardDto);
            crawlingCardDto.setForm(element.select(".cardinfo_top_body dl:contains(Form) dd").text());
            extractEngCardInfoBody(element, crawlingCardDto);
        }


        return crawlingCardDto;
    }

    private void extractCardInfoBottom(Element element, CrawlingCardDto crawlingCardDto) {
        crawlingCardDto.setEffect(changeJapanMiddlePoint(parseElementToPlainText(element.select(".cardinfo_bottom dl:contains(상단 텍스트) dd"))));
        crawlingCardDto.setSourceEffect(changeJapanMiddlePoint(parseElementToPlainText(element.select(".cardinfo_bottom dl:contains(하단 텍스트) dd"))));
        crawlingCardDto.setNote(changeJapanMiddlePoint(element.select(".cardinfo_bottom dl:contains(입수 정보) dd").text()));
    }

    private void extractEngCardInfoBottom(Element element, CrawlingCardDto crawlingCardDto) {
        crawlingCardDto.setEffect(parseElementToPlainText(element.select(".cardinfo_bottom dl dt:matchesOwn(^Effect$) + dd")));
        crawlingCardDto.setSourceEffect(parseElementToPlainText(element.select(".cardinfo_bottom dl:contains(Inherited Effect) dd")));
        crawlingCardDto.setNote(element.select(".cardinfo_bottom dl:contains(Notes) dd").text());
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
        crawlingCardDto.setForm(element.select(".cardinfo_top_body dl:contains(형태) dd").text());
        crawlingCardDto.setImgUrl(element.select(".card_img>img").attr("src"));
        crawlingCardDto.setAttribute(element.select(".cardinfo_top_body dl:contains(속성) dd").text());
        crawlingCardDto.setType(element.select(".cardinfo_top_body dl:contains(유형) dd").text());
        crawlingCardDto.setDP(element.select(".cardinfo_top_body dl:contains(DP) dd").text());
        crawlingCardDto.setPlayCost(element.select(".cardinfo_top_body dl:contains(등장 코스트) dd").text());
        crawlingCardDto.setDigivolveCost1(element.select(".cardinfo_top_body dl:contains(진화 코스트 1) dd").text());
        crawlingCardDto.setDigivolveCost2(element.select(".cardinfo_top_body dl:contains(진화 코스트 2) dd").text());
    }

    private void extractEngCardInfoBody(Element element, CrawlingCardDto crawlingCardDto) {
        Element lvElement = element.selectFirst(".cardlv");
        if (lvElement != null) {
            crawlingCardDto.setLv(lvElement.text());
        }
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
            if (colorText.length > 1) {
                crawlingCardDto.setColor2(colorText[1]);
            }
            if (colorText.length > 2) {
                crawlingCardDto.setColor3(colorText[2]);
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

    @Transactional
    public CardEntity getEnglishCardEntityOrInsert(ReflectCardRequestDto reflectCardRequestDto) {
        EnglishCardEntity englishCardEntity = englishCardRepository.save(EnglishCardEntity.builder()
                .effect(reflectCardRequestDto.getEffect())
                .sourceEffect(reflectCardRequestDto.getSourceEffect())
                .cardName(reflectCardRequestDto.getCardName())
                .build());

        CardEntity cardEntity = cardRepository.findByCardNo(reflectCardRequestDto.getCardNo()).orElseGet(
                () -> {
                    CardEntity save = cardRepository.save(
                            CardEntity.builder()
                                    .sortString(generateSortString(reflectCardRequestDto.getCardNo()))
                                    .cardNo(reflectCardRequestDto.getCardNo())
                                    .dp(reflectCardRequestDto.getDp())
                                    .playCost(reflectCardRequestDto.getPlayCost())
                                    .digivolveCondition1(reflectCardRequestDto.getDigivolveCondition1())
                                    .digivolveCondition2(reflectCardRequestDto.getDigivolveCondition2())
                                    .digivolveCost1(reflectCardRequestDto.getDigivolveCost1())
                                    .digivolveCost2(reflectCardRequestDto.getDigivolveCost2())
                                    .lv(reflectCardRequestDto.getLv())
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

        cardEntity.updateEnglishCard(englishCardEntity);

        return cardEntity;
    }

}
