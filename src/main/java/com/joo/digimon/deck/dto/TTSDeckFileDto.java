package com.joo.digimon.deck.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

import java.util.List;

@Data
public class TTSDeckFileDto {

    public TTSDeckFileDto() {
        this.saveName = "";
        this.date = "";
        this.versionNumber = "";
        this.gameMode = "";
        this.gameType = "";
        this.gameComplexity = "";
        this.tags = new ArrayList<>();
        this.gravity = 0.5;
        this.playArea = 0.5;
        this.table = "";
        this.sky = "";
        this.note = "";
        this.tabStates = new HashMap<>();
        this.luaScript = "";
        this.luaScriptState = "";
        this.xmlUI = "";
        this.objectStates = new ArrayList<>();
        this.objectStates.add(new TTSDeckObject("guid"));
    }

    @JsonProperty("SaveName")
    private String saveName;
    @JsonProperty("Date")
    private String date;
    @JsonProperty("VersionNumber")
    private String versionNumber;
    @JsonProperty("GameMode")
    private String gameMode;
    @JsonProperty("GameType")
    private String gameType;
    @JsonProperty("GameComplexity")
    private String gameComplexity;
    @JsonProperty("Tags")
    private List<Object> tags;
    @JsonProperty("Gravity")
    private double gravity;
    @JsonProperty("PlayArea")
    private double playArea;
    @JsonProperty("Table")
    private String table;
    @JsonProperty("Sky")
    private String sky;
    @JsonProperty("Note")
    private String note;
    @JsonProperty("TabStates")
    private Map<String,String> tabStates;
    @JsonProperty("LuaScript")
    private String luaScript;
    @JsonProperty("LuaScriptState")
    private String luaScriptState;
    @JsonProperty("XmlUI")
    private String xmlUI;
    @JsonProperty("ObjectStates")
    private List<TTSDeckObject> objectStates;

    public void addCard(int cardId, String faceUrl, String backUrl, String guid) {
        this.objectStates.get(0).addCard(cardId, faceUrl, backUrl, guid);
    }


}
