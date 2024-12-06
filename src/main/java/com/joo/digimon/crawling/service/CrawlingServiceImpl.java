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
    @Transactional
    public int crawlAndSaveByUrl(String url, String locale, @Nullable String note) throws IOException {
        List<CrawlingCardEntity> crawlingCardEntities = crawlUrlAndBuildEntityList(url, locale, note);

        crawlingCardEntities
                .forEach(crawlingCardEntity -> {
                    ReflectCardRequestDto reflectCardRequestDto = createReflectCardRequestDto(crawlingCardEntity);
                    saveCardByReflectCardRequest(reflectCardRequestDto);
                    crawlingCardEntity.setIsReflect(true);
                });
        return crawlingCardEntities.size();
    }


    @Transactional
    public void saveCardByReflectCardRequest(ReflectCardRequestDto reflectCardRequestDto) {

        CardEntity cardEntity = getCardEntity(reflectCardRequestDto);
        NoteEntity noteEntity = noteRepository.findByName(reflectCardRequestDto.getNote()).orElseGet(() -> noteRepository.save(NoteEntity.builder().name(reflectCardRequestDto.getNote()).build()));

        if (Boolean.FALSE.equals(reflectCardRequestDto.getIsParallel())) {
            List<CardImgEntity> cardImgEntityList = cardImgRepository.findByCardEntity(cardEntity);
            if (cardImgEntityList.size() > 1) {
//                throw new CardImageException("Duplicate non-parallel card image");
            }
            if (cardImgEntityList.size() == 1) {
                CardImgEntity cardImgEntity = cardImgEntityList.getFirst();

                if (reflectCardRequestDto.getLocale() == Locale.KOR) {
                    if (!Boolean.TRUE.equals(cardImgEntity.getIsEnCard()) && !Boolean.TRUE.equals(cardImgEntity.getIsJpnCard())) {
//                        throw new CardImageException("Non-parallel cards already reflected");
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
                                    .crawlingCardEntity(reflectCardRequestDto.getCrawlingCardEntity())
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
                                        .crawlingCardEntity(reflectCardRequestDto.getCrawlingCardEntity())
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
                        .crawlingCardEntity(reflectCardRequestDto.getCrawlingCardEntity())
                        .cardEntity(cardEntity)
                        .originUrl(reflectCardRequestDto.getOriginUrl())
                        .isEnCard(reflectCardRequestDto.getLocale() == Locale.ENG)
                        .isJpnCard(reflectCardRequestDto.getLocale() == Locale.JPN)
                        .build());
    }

    private CardEntity getCardEntity(ReflectCardRequestDto reflectCardRequestDto) {

        return switch (reflectCardRequestDto.getLocale()) {
            case KOR -> new KorSaveCardProcedure(cardRepository, cardCombineTypeRepository, typeRepository, noteRepository, reflectCardRequestDto).execute();
            case ENG -> new EngSaveCardProcedure(cardRepository, englishCardRepository, cardCombineTypeRepository, typeRepository, noteRepository, reflectCardRequestDto).execute();
            case JPN -> new JpnSaveCardProcedure(cardRepository, japaneseCardRepository, cardCombineTypeRepository, typeRepository, noteRepository, reflectCardRequestDto).execute();
        };
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
        } else if (cardNo.startsWith("LM")) {
            stringBuilder.append("E");
            String[] parts = cardNo.split("-");
            String firstNumberPart = String.format("%03d", Integer.parseInt(parts[1].replaceAll("\\D", "")));
            stringBuilder.append(firstNumberPart);
            return stringBuilder.toString();
        } else if (cardNo.startsWith("P")) {
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
        List<CrawlingCardDto> crawlingCardDtoList = createCrawlingCardDtos(url, locale, note);
        return createCrawlingCardEntities(crawlingCardDtoList);
    }

    private List<CrawlingCardDto> createCrawlingCardDtos(String url, String locale, String note) throws IOException {
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
        return crawlingCardDtoList;
    }

    private List<CrawlingCardEntity> createCrawlingCardEntities(List<CrawlingCardDto> crawlingCardDtoList) {
        List<CrawlingCardEntity> crawlingCardEntities = new ArrayList<>();

        for (CrawlingCardDto crawlingCardDto : crawlingCardDtoList) {
            Optional<CrawlingCardEntity> crawlingCardEntity = crawlingCardRepository.findByImgUrlAndLocale(crawlingCardDto.getImgUrl(), crawlingCardDto.getLocale());
            if (crawlingCardEntity.isEmpty()) {
                crawlingCardEntities.add(crawlingCardRepository.save(CrawlingCardEntity.buildCrawlingCardEntity(crawlingCardDto)));
            }
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
