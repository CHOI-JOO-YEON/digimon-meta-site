package com.joo.digimon.crawling.service;

import com.joo.digimon.card.model.*;
import com.joo.digimon.card.repository.*;
import com.joo.digimon.crawling.dto.*;
import com.joo.digimon.crawling.model.CrawlingCardEntity;
import com.joo.digimon.crawling.procedure.crwaling.EngCrawlingProcedure;
import com.joo.digimon.crawling.procedure.crwaling.JpnCrawlingProcedure;
import com.joo.digimon.crawling.procedure.crwaling.KorCrawlingProcedure;
import com.joo.digimon.crawling.procedure.img.KorCardImgProcessor;
import com.joo.digimon.crawling.procedure.img.OtherLocaleCardImgProcessor;
import com.joo.digimon.crawling.procedure.parse.EngCardParseProcedure;
import com.joo.digimon.crawling.procedure.parse.JpnCardParseProcedure;
import com.joo.digimon.crawling.procedure.parse.KorCardParseProcedure;
import com.joo.digimon.crawling.procedure.save.EngSaveCardProcedure;
import com.joo.digimon.crawling.procedure.save.JpnSaveCardProcedure;
import com.joo.digimon.crawling.procedure.save.KorSaveCardProcedure;
import com.joo.digimon.crawling.repository.CrawlingCardRepository;
import com.joo.digimon.global.enums.Locale;
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
    private final EnglishCardRepository englishCardRepository;
    private final JapaneseCardRepository japaneseCardRepository;

    @Override
    @Transactional
    public int crawlAndSaveByUrl(String url, Locale locale, @Nullable String note) throws IOException {
        List<CrawlingCardEntity> crawlingCardEntities = crawlUrlAndBuildEntityList(url, locale, note);

        crawlingCardEntities
                .forEach(crawlingCardEntity -> {
                    ReflectCardRequestDto reflectCardRequestDto = createReflectCardRequestDto(crawlingCardEntity);
                    saveCardByReflectCardRequest(reflectCardRequestDto);
                    crawlingCardEntity.setIsReflect(true);
                });
        return crawlingCardEntities.size();
    }


    private void saveCardByReflectCardRequest(ReflectCardRequestDto reflectCardRequestDto) {

        CardEntity cardEntity = getCardEntity(reflectCardRequestDto);
        NoteEntity noteEntity = noteRepository.findByName(reflectCardRequestDto.getNote()).orElseGet(() -> noteRepository.save(NoteEntity.builder().name(reflectCardRequestDto.getNote()).build()));
        switch (reflectCardRequestDto.getLocale()) {
            case ENG, JPN ->
                    new OtherLocaleCardImgProcessor(cardImgRepository)
                            .process(reflectCardRequestDto, cardEntity, noteEntity);
            case KOR ->
                    new KorCardImgProcessor(cardImgRepository).process(reflectCardRequestDto, cardEntity, noteEntity);
        }
    }

    private CardEntity getCardEntity(ReflectCardRequestDto reflectCardRequestDto) {
        return switch (reflectCardRequestDto.getLocale()) {
            case KOR ->
                    new KorSaveCardProcedure(cardRepository, cardCombineTypeRepository, typeRepository, noteRepository, reflectCardRequestDto).execute();
            case ENG ->
                    new EngSaveCardProcedure(cardRepository, englishCardRepository, cardCombineTypeRepository, typeRepository, noteRepository, reflectCardRequestDto).execute();
            case JPN ->
                    new JpnSaveCardProcedure(cardRepository, japaneseCardRepository, cardCombineTypeRepository, typeRepository, noteRepository, reflectCardRequestDto).execute();
        };
    }


    private List<CrawlingCardEntity> crawlUrlAndBuildEntityList(String url, Locale locale, @Nullable String note) throws IOException {
        List<CrawlingCardDto> crawlingCardDtoList = createCrawlingCardDtos(url, locale, note);
        return createCrawlingCardEntities(crawlingCardDtoList);
    }

    private List<CrawlingCardDto> createCrawlingCardDtos(String url, Locale locale, String note) throws IOException {
        List<Document> documentListByFirstPageUrl = getDocumentListByFirstPageUrl(url);

        List<Element> cardElement = new ArrayList<>();
        for (Document document : documentListByFirstPageUrl) {
            cardElement.addAll(getCardElementsByDocument(document));
        }
        List<CrawlingCardDto> crawlingCardDtoList = new ArrayList<>();
        for (Element element : cardElement) {
            CrawlingCardDto crawlingCardDto = crawlingCardByElement(element, locale);

            if ((note == null || crawlingCardDto.getNote().equals(note))
                    && (locale == Locale.KOR || !crawlingCardDto.getIsParallel())) {
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

    private List<Document> getDocumentListByFirstPageUrl(String url) throws IOException {
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

    private List<Element> getCardElementsByDocument(Document document) {
        return document.select(".cardlistCol .popup");
    }

    private CrawlingCardDto crawlingCardByElement(Element element, Locale locale) {
        return switch (locale) {
            case KOR -> new KorCrawlingProcedure(element).execute();
            case ENG -> new EngCrawlingProcedure(element).execute();
            case JPN -> new JpnCrawlingProcedure(element).execute();
        };
    }

    private ReflectCardRequestDto createReflectCardRequestDto(CrawlingCardEntity crawlingCardEntity) {
        return switch (crawlingCardEntity.getLocale()) {
            case KOR -> new KorCardParseProcedure(crawlingCardEntity).execute();
            case ENG -> new EngCardParseProcedure(crawlingCardEntity).execute();
            case JPN -> new JpnCardParseProcedure(crawlingCardEntity).execute();
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

    @Transactional
    @Override
    public void imageMove()
    {
        List<CardImgEntity> cardImgEntities = cardImgRepository.findByIsEnCardTrueOrIsJpnCardTrue();
        cardImgEntities.forEach(
                cardImgEntity -> {
                    if(Boolean.TRUE.equals(cardImgEntity.getIsEnCard())) {
                        cardImgEntity.getCardEntity().getEnglishCard().updateUploadUrl(cardImgEntity.getUploadUrl());
                        cardImgEntity.getCardEntity().getEnglishCard().updateOriginUrl(cardImgEntity.getOriginUrl());


                    }else if(Boolean.TRUE.equals(cardImgEntity.getIsJpnCard())) {
                        cardImgEntity.getCardEntity().getJapaneseCardEntity().updateUploadUrl(cardImgEntity.getUploadUrl());
                        cardImgEntity.getCardEntity().getJapaneseCardEntity().updateOriginUrl(cardImgEntity.getOriginUrl());
                    }

                    cardImgEntity.updateUploadUrl(null);
                    cardImgEntity.updateOriginUrl(null);
                }
        );
    }
}
