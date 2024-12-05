package com.joo.digimon.crawling.service;

import com.joo.digimon.card.model.*;
import com.joo.digimon.card.repository.*;
import com.joo.digimon.crawling.dto.*;
import com.joo.digimon.crawling.model.CrawlingCardEntity;
import com.joo.digimon.crawling.model.DeletedEnCardImg;
import com.joo.digimon.crawling.procedure.*;
import com.joo.digimon.crawling.repository.CrawlingCardRepository;
import com.joo.digimon.crawling.repository.DeletedEnCardImgRepository;
import com.joo.digimon.global.enums.Locale;
import com.joo.digimon.global.exception.model.CardImageException;
import com.joo.digimon.global.exception.model.CardParseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.io.IOException;
import java.sql.Ref;
import java.time.LocalDate;
import java.util.*;

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
    private final DeletedEnCardImgRepository deletedEnCardImgRepository;
    private final EnglishCardRepository englishCardRepository;
    private final JapaneseCardRepository japaneseCardRepository;

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
                ReflectCardRequestDto reflectCardRequestDto = createReflectCardRequestDto(crawlingCardEntity);

                if(reflectCardRequestDto != null) {
                    saveCardByReflectCardRequest(reflectCardRequestDto);
                    crawlingResultDto.successCountIncrease();
                    crawlingCardEntity.setIsReflect(true);
                }
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
    public void saveCardByReflectCardRequest(ReflectCardRequestDto reflectCardRequestDto) throws CardParseException, CardImageException {
        CrawlingCardEntity crawlingCardEntity = crawlingCardRepository.findById(reflectCardRequestDto.getId()).orElseThrow();

        CardEntity cardEntity;
        if (reflectCardRequestDto.getLocale() == Locale.ENG) {
            cardEntity = getEnglishCardEntityOrInsert(reflectCardRequestDto);
        } else if (reflectCardRequestDto.getLocale() == Locale.JPN) {
            cardEntity = getJapaneseCardEntityOrInsert(reflectCardRequestDto);
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

                if (reflectCardRequestDto.getLocale() == Locale.KOR) {
                    if (!Boolean.TRUE.equals(cardImgEntity.getIsEnCard()) && !Boolean.TRUE.equals(cardImgEntity.getIsJpnCard())) {
                        throw new CardImageException("Non-parallel cards already reflected");
                    }
                    deletedEnCardImgRepository.save(DeletedEnCardImg.builder()
                            .cardEntity(cardEntity)
                            .crawlingCardEntity(cardImgEntity.getCrawlingCardEntity())
                            .uploadUrl(cardImgEntity.getUploadUrl())
                            .smallImgUrl(cardImgEntity.getSmallImgUrl())
                            .originUrl(cardImgEntity.getOriginUrl())
                            .noteEntity(cardImgEntity.getNoteEntity())
                            .build());

                    cardImgRepository.save(
                            CardImgEntity.builder()
                                    .id(cardImgEntity.getId())
                                    .isParallel(reflectCardRequestDto.getIsParallel())
                                    .noteEntity(noteEntity)
                                    .crawlingCardEntity(crawlingCardEntity)
                                    .cardEntity(cardEntity)
                                    .originUrl(reflectCardRequestDto.getOriginUrl())
                                    .build());

                } else if (reflectCardRequestDto.getLocale() == Locale.ENG) {
                    if (cardImgEntity.getIsJpnCard()) {
                        cardImgRepository.save(
                                CardImgEntity.builder()
                                        .id(cardImgEntity.getId())
                                        .isParallel(reflectCardRequestDto.getIsParallel())
                                        .noteEntity(noteEntity)
                                        .crawlingCardEntity(crawlingCardEntity)
                                        .cardEntity(cardEntity)
                                        .originUrl(reflectCardRequestDto.getOriginUrl())
                                        .isEnCard(true)
                                        .build());
                    }
                }
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
                        .isEnCard(reflectCardRequestDto.getLocale() == Locale.ENG)
                        .isJpnCard(reflectCardRequestDto.getLocale() == Locale.JPN)
                        .build());
    }


    @Transactional
    public CardEntity getJapaneseCardEntityOrInsert(ReflectCardRequestDto reflectCardRequestDto) {
        JapaneseCardEntity japaneseCardEntity = japaneseCardRepository.save(
                JapaneseCardEntity.builder()
                        .effect(reflectCardRequestDto.getEffect())
                        .sourceEffect(reflectCardRequestDto.getSourceEffect())
                        .cardName(reflectCardRequestDto.getCardName())
                        .build());

        CardEntity cardEntity = cardRepository.findByCardNo(reflectCardRequestDto.getCardNo()).orElseGet(
                () -> cardRepository.save(
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
                                .color3(reflectCardRequestDto.getColor3())
                                .isOnlyEnCard(true)
                                .releaseDate(LocalDate.of(9999, 12, 31))
                                .build())
        );


        Set<CardCombineTypeEntity> cardCombineTypeEntities = new HashSet<>();

        if (cardEntity.getCardCombineTypeEntities() == null || cardEntity.getCardCombineTypeEntities().isEmpty()) {
            for (String type : reflectCardRequestDto.getTypes()) {
                TypeEntity typeEntity = typeRepository.findByJpnName(type).orElseGet(() ->
                        typeRepository.save(
                                TypeEntity.builder()
                                        .jpnName(type)
                                        .build())
                );

                cardCombineTypeEntities.add(
                        cardCombineTypeRepository.save(
                                CardCombineTypeEntity.builder()
                                        .cardEntity(cardEntity)
                                        .typeEntity(typeEntity)
                                        .build())
                );
            }
            cardEntity.updateCardCombineTypes(cardCombineTypeEntities);
        }

        cardEntity.updateJapaneseCard(japaneseCardEntity);
        japaneseCardEntity.updateCardEntity(cardEntity);
        return cardEntity;
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

                if (save.getCardCombineTypeEntities() == null || save.getCardCombineTypeEntities().isEmpty()) {
                    for (String type : reflectCardRequestDto.getTypes()) {
                        TypeEntity typeEntity = typeRepository.findByName(type).orElseGet(() -> typeRepository.save(TypeEntity.builder().name(type).build()));
                        cardCombineTypeRepository.save(CardCombineTypeEntity.builder().cardEntity(save).typeEntity(typeEntity).build());
                    }
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
        } else if(cardNo.startsWith("LM")) {
            stringBuilder.append("E");
            String[] parts = cardNo.split("-");
            String firstNumberPart = String.format("%03d", Integer.parseInt(parts[1].replaceAll("\\D", "")));
            stringBuilder.append(firstNumberPart);
            return stringBuilder.toString();
        }
        else if (cardNo.startsWith("P")) {
            stringBuilder.append("Z");
            String[] parts = cardNo.split("-");
            String firstNumberPart = String.format("%03d", Integer.parseInt(parts[1].replaceAll("\\D", "")));
            stringBuilder.append(firstNumberPart);
            return stringBuilder.toString();
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
                    && (locale.equals("KOR") || !crawlingCardDto.getIsParallel())) {
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
        return crawlingCardRepository.findByImgUrlAndLocale(crawlingCardDto.getImgUrl(), crawlingCardDto.getLocale()).orElseGet(() -> crawlingCardRepository.save(new CrawlingCardEntity(crawlingCardDto)));
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
        return switch (locale) {
            case "KOR" -> new KorCrawlingProcedure(element).execute();
            case "ENG" -> new EngCrawlingProcedure(element).execute();
            case "JPN" -> new JpnCrawlingProcedure(element).execute();
            default -> null;
        };
    }

    private ReflectCardRequestDto createReflectCardRequestDto(CrawlingCardEntity crawlingCardEntity) {
        return switch (crawlingCardEntity.getLocale()) {
            case Locale.KOR -> new KorCardParseProcedure(crawlingCardEntity).execute();
            case Locale.ENG -> new EngCardParseProcedure(crawlingCardEntity).execute();
            case Locale.JPN -> new JpnCardParseProcedure(crawlingCardEntity).execute();
        };
    }

    @Transactional
    public CardEntity getEnglishCardEntityOrInsert(ReflectCardRequestDto reflectCardRequestDto) {
        EnglishCardEntity englishCardEntity = englishCardRepository.save(EnglishCardEntity.builder()
                .effect(reflectCardRequestDto.getEffect())
                .sourceEffect(reflectCardRequestDto.getSourceEffect())
                .cardName(reflectCardRequestDto.getCardName())
                .build());

        CardEntity cardEntity = cardRepository.findByCardNo(reflectCardRequestDto.getCardNo()).orElseGet(
                () -> cardRepository.save(
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
                                .color3(reflectCardRequestDto.getColor3())
                                .isOnlyEnCard(true)
                                .releaseDate(LocalDate.of(9999, 12, 31))
                                .build())
        );


        Set<CardCombineTypeEntity> cardCombineTypeEntities = new HashSet<>();

        if (cardEntity.getCardCombineTypeEntities() == null || cardEntity.getCardCombineTypeEntities().isEmpty()) {
            for (String type : reflectCardRequestDto.getTypes()) {
                TypeEntity typeEntity = typeRepository.findByEngName(type).orElseGet(() ->
                        typeRepository.save(
                                TypeEntity.builder()
                                        .engName(type)
                                        .build())
                );

                cardCombineTypeEntities.add(
                        cardCombineTypeRepository.save(
                                CardCombineTypeEntity.builder()
                                        .cardEntity(cardEntity)
                                        .typeEntity(typeEntity)
                                        .build())
                );
            }
            cardEntity.updateCardCombineTypes(cardCombineTypeEntities);
        }

        cardEntity.updateEnglishCard(englishCardEntity);
        englishCardEntity.updateCardEntity(cardEntity);
        return cardEntity;
    }


    @Transactional
    public CrawlingResultDto reCrawlingByLocale(Locale locale) {
        CrawlingResultDto crawlingResultDto = new CrawlingResultDto();
        List<CrawlingCardEntity> crawlingCardEntities = crawlingCardRepository.findByLocale(locale);

        for (CrawlingCardEntity crawlingCardEntity : crawlingCardEntities) {
            try {
                ReflectCardRequestDto reflectCardRequestDto = createReflectCardRequestDto(crawlingCardEntity);
                CardEntity cardEntity = cardRepository.findByCardNo(crawlingCardEntity.getCardNo()).orElseThrow();

                if (locale.equals("ENG")) {
                    if (cardEntity.getEnglishCard() == null) {
                        EnglishCardEntity englishCardEntity = englishCardRepository.save(
                                EnglishCardEntity.builder()
                                        .cardEntity(cardEntity)
                                        .cardName(crawlingCardEntity.getCardName())
                                        .effect(reflectCardRequestDto.getEffect())
                                        .sourceEffect(reflectCardRequestDto.getSourceEffect())
                                        .build());
                        cardEntity.updateEnglishCard(englishCardEntity);
                        crawlingResultDto.successCountIncrease();
                    }
                }
            } catch (Exception e) {
                crawlingCardEntity.updateErrorMessage(e.getMessage());
                crawlingResultDto.addFailedCrawling(new CrawlingCardDto(crawlingCardEntity));
                log.error("{} 에서 발생 {} ", crawlingCardEntity, e);
            }
        }
        return crawlingResultDto;
    }
    @Transactional
    public CrawlingResultDto reCrawlingHardByLocale(Locale locale) {
        CrawlingResultDto crawlingResultDto = new CrawlingResultDto();
        List<CrawlingCardEntity> crawlingCardEntities = crawlingCardRepository.findByLocale(locale);

        for (CrawlingCardEntity crawlingCardEntity : crawlingCardEntities) {
            try {
                ReflectCardRequestDto reflectCardRequestDto = createReflectCardRequestDto(crawlingCardEntity);
                CardEntity cardEntity = cardRepository.findByCardNo(crawlingCardEntity.getCardNo()).orElseThrow();

                if (locale.equals("ENG")) {
                    if (cardEntity.getEnglishCard() == null) {
                        EnglishCardEntity englishCardEntity = englishCardRepository.save(
                                EnglishCardEntity.builder()
                                        .cardEntity(cardEntity)
                                        .cardName(crawlingCardEntity.getCardName())
                                        .effect(reflectCardRequestDto.getEffect())
                                        .sourceEffect(reflectCardRequestDto.getSourceEffect())
                                        .build());
                        cardEntity.updateEnglishCard(englishCardEntity);
                        crawlingResultDto.successCountIncrease();
                    }
                }
            } catch (Exception e) {
                crawlingCardEntity.updateErrorMessage(e.getMessage());
                crawlingResultDto.addFailedCrawling(new CrawlingCardDto(crawlingCardEntity));
                log.error("{} 에서 발생 {} ", crawlingCardEntity, e);
            }
        }
        return crawlingResultDto;
    }
}
