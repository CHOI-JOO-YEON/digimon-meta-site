package com.joo.digimon.card.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.joo.digimon.card.dto.card.CardAdminPutDto;
import com.joo.digimon.card.dto.card.CardVo;
import com.joo.digimon.card.dto.card.TraitDto;
import com.joo.digimon.card.dto.card.TypeMergeRequestDto;
import com.joo.digimon.card.dto.note.CreateNoteDto;
import com.joo.digimon.card.dto.note.ResponseNoteDto;
import com.joo.digimon.card.dto.note.UpdateNoteDto;
import com.joo.digimon.card.dto.type.TypeDto;
import com.joo.digimon.card.model.*;
import com.joo.digimon.card.repository.*;
import com.joo.digimon.global.enums.Attribute;
import com.joo.digimon.global.enums.Form;
import com.joo.digimon.global.enums.Locale;
import com.joo.digimon.global.exception.model.CanNotDeleteException;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CardAdminServiceImpl implements CardAdminService {

    @Value("${repository.url}")
    private String repositoryUrl;

    @Value("${github.username}")
    private String username;

    @Value("${github.access-token}")
    private String accessToken;

    @Value("${domain.url}")
    private String prefixUrl;
    
    private static final String localPath = "repo";
    private static final String cardsPath = localPath + "/assets/data/cards.json";
    private static final String notesPath = localPath + "/assets/data/notes.json";


    private final CardImgRepository cardImgRepository;
    private final NoteRepository noteRepository;
    private final EnglishCardRepository englishCardRepository;
    private final CardCombineTypeRepository cardCombineTypeRepository;
    private final TypeRepository typeRepository;
    private final JapaneseCardRepository japaneseCardRepository;
    private final EntityManager entityManager;
    private final ObjectMapper objectMapper;


    @Override
    @Transactional
    public List<ResponseNoteDto> createNote(CreateNoteDto createNoteDto) {
        noteRepository.save(NoteEntity.builder()
                .name(createNoteDto.getName())
                .cardOrigin(createNoteDto.getCardOrigin())
                .releaseDate(createNoteDto.getReleaseDate())
                .build());

        return getAllResponseNoteDto();
    }

    @Override
    @Transactional
    public List<ResponseNoteDto> deleteNote(Integer noteId) {
        Optional<NoteEntity> noteEntityOptional = noteRepository.findById(noteId);
        if (noteEntityOptional.isEmpty()) {
            throw new NoSuchElementException();
        }
        if (noteEntityOptional.get().getCardImgEntities().size() > 1) {
            throw new CanNotDeleteException("연관 관계인 카드가 있어 삭제에 실패했습니다.");
        }

        noteRepository.deleteById(noteId);

        return getAllResponseNoteDto();
    }

    @Override
    @Transactional
    public List<ResponseNoteDto> putNotes(List<UpdateNoteDto> updateNoteDtoList) {
        for (UpdateNoteDto updateNoteDto : updateNoteDtoList) {
            Optional<NoteEntity> note = noteRepository.findById(updateNoteDto.getNoteId());
            if (note.isEmpty()) {
                throw new NoSuchElementException();
            }
            note.get().putNote(updateNoteDto);
        }

        return getAllResponseNoteDto();
    }

    @Transactional
    @Override
    public void updateCards(List<CardAdminPutDto> cardAdminPutDtoList) {
        for (CardAdminPutDto cardAdminPutDto : cardAdminPutDtoList) {
            CardImgEntity cardImgEntity = getCardImgEntity(cardAdminPutDto);
            updateCardEnglishProperty(cardAdminPutDto, cardImgEntity);
            updateCardJapaneseProperty(cardAdminPutDto, cardImgEntity);
            cardImgEntity.update(cardAdminPutDto);
            NoteEntity noteEntity = noteRepository.findById(cardAdminPutDto.getNoteId()).orElseThrow();
            cardImgEntity.updateNote(noteEntity);
            updateType(cardAdminPutDto, cardImgEntity);
        }
    }


    @Override
    public List<TypeDto> getAllType() {
        List<TypeEntity> types = typeRepository.findAll(false);
        List<TypeDto> typeDtoList = new ArrayList<>();
        for (TypeEntity type : types) {
            typeDtoList.add(new TypeDto(type, false));
        }
        return typeDtoList;
    }

    @Override
    public List<TypeDto> getAllTypeDetail() {
        List<TypeEntity> types = typeRepository.findAll(true);
        List<TypeDto> typeDtoList = new ArrayList<>();
        for (TypeEntity type : types) {
            typeDtoList.add(new TypeDto(type, true));
        }
        return typeDtoList;
    }


    @Override
    @Transactional
    public List<TypeDto> deleteType(Integer typeId) {
        Optional<TypeEntity> typeEntityOptional = typeRepository.findById(typeId);
        if (typeEntityOptional.isEmpty()) {
            throw new NoSuchElementException();
        }
        if (typeEntityOptional.get().getCardCombineTypes().size() > 1) {
            throw new CanNotDeleteException("연관 관계인 카드가 있어 삭제에 실패했습니다.");
        }
        typeRepository.deleteById(typeId);
        return getAllType();
    }

    @Override
    @Transactional
    public List<TypeDto> putTypes(List<TypeDto> typeDtoList) {
        for (TypeDto typeDto : typeDtoList) {
            Optional<TypeEntity> typeEntityOptional = typeRepository.findById(typeDto.getTypeId());
            if (typeEntityOptional.isEmpty()) {
                throw new NoSuchElementException();
            }
            typeEntityOptional.get().putType(typeDto);
        }
        return getAllType();
    }


    @Transactional
    public CardImgEntity getCardImgEntity(CardAdminPutDto cardAdminPutDto) {
        Optional<CardImgEntity> optionalCardImg = cardImgRepository.findById(cardAdminPutDto.getCardId());
        if (optionalCardImg.isEmpty()) {
            throw new NoSuchElementException();
        }
        return optionalCardImg.get();
    }

    @Transactional
    public void updateType(CardAdminPutDto cardAdminPutDto, CardImgEntity cardImgEntity) {
        if (cardAdminPutDto.getTypes() != null) {
            Set<String> types = cardAdminPutDto.getTypes();
            CardEntity cardEntity = cardImgEntity.getCardEntity();
            Set<CardCombineTypeEntity> existingEntities = cardEntity.getCardCombineTypeEntities();

            existingEntities.clear(); 

            for (String type : types) {
                TypeEntity typeEntity = typeRepository.findByName(type)
                        .orElseGet(() -> typeRepository.save(TypeEntity.builder().name(type).build()));

                CardCombineTypeEntity newEntity = CardCombineTypeEntity.builder()
                        .cardEntity(cardEntity)
                        .typeEntity(typeEntity)
                        .build();

                cardEntity.addCardCombineType(newEntity);
            }

        }
    }

    @Transactional
    public void updateCardEnglishProperty(CardAdminPutDto cardAdminPutDto, CardImgEntity cardImgEntity) {
        if (!isEngPresent(cardAdminPutDto)) {
            return;
        }
        EnglishCardEntity englishCard = cardImgEntity.getCardEntity().getEnglishCard();
        if (englishCard == null) {
            englishCard = EnglishCardEntity.builder().cardEntity(cardImgEntity.getCardEntity()).build();
        }
        englishCard.update(cardAdminPutDto);
        englishCardRepository.save(englishCard);
    }


    @Transactional
    public void updateCardJapaneseProperty(CardAdminPutDto cardAdminPutDto, CardImgEntity cardImgEntity) {
        if (!isJpnPresent(cardAdminPutDto)) {
            return;
        }
        JapaneseCardEntity japaneseCardEntity = cardImgEntity.getCardEntity().getJapaneseCardEntity();
        if (japaneseCardEntity == null) {
            japaneseCardEntity = JapaneseCardEntity.builder().cardEntity(cardImgEntity.getCardEntity()).build();
        }
        japaneseCardEntity.update(cardAdminPutDto);
        japaneseCardRepository.save(japaneseCardEntity);
    }
    private boolean isJpnPresent(CardAdminPutDto cardAdminPutDto) {
        return cardAdminPutDto.getCardJpnName()!=null || cardAdminPutDto.getJpnEffect()!=null || cardAdminPutDto.getJpnSourceEffect()!=null;
    }
    private boolean isEngPresent(CardAdminPutDto cardAdminPutDto) {
        return cardAdminPutDto.getCardEngName()!=null || cardAdminPutDto.getEngEffect()!=null || cardAdminPutDto.getEngSourceEffect()!=null;
    }

    private List<ResponseNoteDto> getAllResponseNoteDto() {
        List<ResponseNoteDto> responseNoteDtos = new ArrayList<>();
        List<NoteEntity> noteEntities = noteRepository.findAll();
        for (NoteEntity noteEntity : noteEntities) {
            responseNoteDtos.add(new ResponseNoteDto(noteEntity));
        }
        return responseNoteDtos;
    }

    @Override
    public List<ResponseNoteDto> getNotes() {
        List<ResponseNoteDto> noteDtoList = new ArrayList<>();
        List<NoteEntity> noteEntityList = noteRepository.findAll();
        for (NoteEntity noteEntity : noteEntityList) {
            noteDtoList.add(new ResponseNoteDto(noteEntity));
        }

        return noteDtoList;
    }

    @Override
    @Transactional
    public void mergeTypeToKorean(TypeMergeRequestDto dto) {
        TypeEntity baseType = typeRepository.findById(dto.getBaseTypeId()).orElseThrow();
        TypeEntity targetType = typeRepository.findById(dto.getTargetTypeId()).orElseThrow();

        baseType.getCardCombineTypes()
                .forEach(cardCombineType -> cardCombineType.updateType(targetType));

        if(dto.getLocale().equals(Locale.ENG)){
            targetType.updateEngName(baseType.getEngName());
        }
        else if(dto.getLocale().equals(Locale.JPN)){
            targetType.updateJpnName(baseType.getJpnName());
        }

        typeRepository.delete(baseType);
    }

    @Override
    @Transactional
    public void deleteDuplicateCardCombineType() {
        QCardCombineTypeEntity cct = QCardCombineTypeEntity.cardCombineTypeEntity;
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        List<Tuple> duplicates = queryFactory
                .select(cct.cardEntity.id, cct.typeEntity.id, cct.id.count())
                .from(cct)
                .groupBy(cct.cardEntity.id, cct.typeEntity.id)
                .having(cct.id.count().gt(1))
                .fetch();

        for (Tuple tuple : duplicates) {
            Integer cardId = tuple.get(cct.cardEntity.id);
            Integer typeId = tuple.get(cct.typeEntity.id);

            List<CardCombineTypeEntity> entities = queryFactory
                    .selectFrom(cct)
                    .where(cct.cardEntity.id.eq(cardId)
                            .and(cct.typeEntity.id.eq(typeId)))
                    .orderBy(cct.id.asc())
                    .fetch();

            for (int i = 1; i < entities.size(); i++) {
                queryFactory
                        .delete(cct)
                        .where(cct.id.eq(entities.get(i).getId()))
                        .execute();
            }
        }
    }

    public Boolean createCardJsonUpdateToGitHubPR(String message) {
        String branchName = "auto/" + UUID.randomUUID();
        File localRepoDir = new File(localPath);
        Git git = null;

        try {
            if (localRepoDir.exists() && localRepoDir.isDirectory()) {
                git = Git.open(localRepoDir);
            } else {
                git = Git.cloneRepository()
                        .setURI(repositoryUrl)
                        .setDirectory(localRepoDir)
                        .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, accessToken))
                        .call();
            }

            git.checkout().setName("master").call();

            git.fetch()
                    .setRemote("origin")
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, accessToken))
                    .call();

            git.reset()
                    .setMode(ResetCommand.ResetType.HARD)
                    .setRef("origin/master")
                    .call();

            git.checkout().setCreateBranch(true).setName(branchName).call();

            File file = new File(cardsPath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try (FileWriter writer = new FileWriter(file, false)) {
                writer.write(getCardsJson());
            }

            file = new File(notesPath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try (FileWriter writer = new FileWriter(file, false)) {
                writer.write(getNotesJson());
            }
            
            

            git.add()
                    .addFilepattern(".")
                    .call();

            git.commit()
                    .setMessage(message)
                    .call();

            git.push()
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, accessToken))
                    .setRemote("origin")
                    .add(branchName)
                    .call();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (git != null) {
                git.close();
            }
        }
    }

    @Override
    public TraitDto getAllTraits() {
        List<TraitDto.TraitDtoType> dtoTypes = typeRepository.findAll(false).stream()
                .map(t -> new TraitDto.TraitDtoType(t.getName(), t.getId()))
                .collect(Collectors.toList());

        return TraitDto.builder()
                .attributes(Arrays.stream(Attribute.values()).map(Enum::name).collect(Collectors.toList()))
                .forms(Stream.of(Form.values()).filter(f -> f != Form.BABY).map(f -> new TraitDto.TraitDtoForm(f.name(), f.getKor())).collect(Collectors.toList()))
                .types(dtoTypes)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public TypeDto getCardByTypeId(Integer typeId) {
        QCardCombineTypeEntity cct = QCardCombineTypeEntity.cardCombineTypeEntity;
        QCardEntity card = QCardEntity.cardEntity;

        List<String> cardNos = new JPAQuery<String>(entityManager)
                .select(card.cardNo)
                .from(cct)
                .join(cct.cardEntity, card)
                .where(cct.typeEntity.id.eq(typeId))
                .distinct()
                .orderBy(card.cardNo.asc())
                .fetch();

        Long total = new JPAQuery<Long>(entityManager)
                .select(card.id.countDistinct())
                .from(cct)
                .join(cct.cardEntity, card)
                .where(cct.typeEntity.id.eq(typeId))
                .fetchOne();

        TypeDto dto = new TypeDto();
        dto.setTypeId(typeId);
        dto.setCardNos(cardNos);
        dto.setCardCount(total == null ? 0 : total.intValue());
        return dto;
    }

    private String getCardsJson() throws JsonProcessingException {
        List<CardImgEntity> cardImgEntities = cardImgRepository.findAll();
        List<CardVo> cardVos = new ArrayList<>();

        for (CardImgEntity cardImgEntity : cardImgEntities) {
            cardVos.add(new CardVo(cardImgEntity, prefixUrl));
        }
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        return objectMapper.writeValueAsString(cardVos);
    }

    private String getNotesJson() throws JsonProcessingException {
        List<ResponseNoteDto> notes = new ArrayList<>();
        List<NoteEntity> noteEntityList = noteRepository.findByIsDisableFalseOrIsDisableNullOrderByReleaseDateAscPriorityAsc();
        for (NoteEntity noteEntity : noteEntityList) {
            notes.add(new ResponseNoteDto(noteEntity));
        }
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        return objectMapper.writeValueAsString(notes);
    }
}
