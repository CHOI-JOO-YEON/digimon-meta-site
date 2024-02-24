package com.joo.digimon.deck.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class TTSCardObject {

    public TTSCardObject(String guid, int cardId, TTSCustomDeck customDeck) {
        this.guid = guid;
        this.name = "CardCustom";
        this.transform = new TTSCardObject.Transform( );
        this.nickname = "";
        this.description = "";
        this.gMNotes = "";
        this.altLookAngle = new TTSCardObject.AltLookAngle();
        this.colorDiffuse = new TTSCardObject.ColorDiffuse();
        this.layoutGroupSortIndex = 0;
        this.value = 0;
        this.locked = false;
        this.grid = true;
        this.snap = true;
        this.ignoreFoW = false;
        this.measureMovement = false;
        this.dragSelectable = true;
        this.autoraise = true;
        this.sticky = true;
        this.tooltip = true;
        this.gridProjection = false;
        this.hideWhenFaceDown = true;
        this.hands = true;
        this.sidewaysCard = false;
        this.luaScript = "";
        this.luaScriptState = "";
        this.xmlUI = "";
        this.customDecks = new HashMap<>();
        this.cardId = cardId*100;
        this.customDecks.put(String.valueOf(cardId), customDeck);

    }

    @JsonProperty("GUID")
    private String guid;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("Transform")
    private Transform transform;
    @JsonProperty("Nickname")
    private String nickname;
    @JsonProperty("Description")
    private String description;
    @JsonProperty("GMNotes")
    private String gMNotes;
    @JsonProperty("AltLookAngle")
    private AltLookAngle altLookAngle;
    @JsonProperty("ColorDiffuse")
    private ColorDiffuse colorDiffuse;
    @JsonProperty("LayoutGroupSortIndex")
    private int layoutGroupSortIndex;
    @JsonProperty("Value")
    private int value;
    @JsonProperty("Locked")
    private boolean locked;
    @JsonProperty("Grid")
    private boolean grid;
    @JsonProperty("Snap")
    private boolean snap;
    @JsonProperty("IgnoreFoW")
    private boolean ignoreFoW;
    @JsonProperty("MeasureMovement")
    private boolean measureMovement;
    @JsonProperty("DragSelectable")
    private boolean dragSelectable;
    @JsonProperty("Autoraise")
    private boolean autoraise;
    @JsonProperty("Sticky")
    private boolean sticky;
    @JsonProperty("Tooltip")
    private boolean tooltip;
    @JsonProperty("GridProjection")
    private boolean gridProjection;
    @JsonProperty("HideWhenFaceDown")
    private boolean hideWhenFaceDown;
    @JsonProperty("Hands")
    private boolean hands;
    @JsonProperty("SidewaysCard")
    private boolean sidewaysCard;
    @JsonProperty("LuaScript")
    private String luaScript;
    @JsonProperty("LuaScriptState")
    private String luaScriptState;
    @JsonProperty("XmlUI")
    private String xmlUI;

    @JsonProperty("CardId")
    private int cardId;
    @JsonProperty("CustomDeck")
    protected Map<String, TTSCustomDeck> customDecks;


    @Getter
    @Setter
    private static class ColorDiffuse {
        private double r;
        private double g;
        private double b;

        public ColorDiffuse() {
            this.r = 0.713235259;
            this.g = 0.713235259;
            this.b = 0.713235259;
        }
    }

    @Getter
    @Setter
    private static class Transform {
        private double posX;
        private double posY;
        private double posZ;
        private double rotX;
        private double rotY;
        private double rotZ;
        private double scaleX;
        private double scaleY;
        private double scaleZ;

        private Transform() {
            this.posX = 0;
            this.posY = 0;
            this.posZ = 0;
            this.rotX = 0;
            this.rotY = 180;
            this.rotZ = 0;
            this.scaleX = 2.35946536;
            this.scaleY = 1.0;
            this.scaleZ = 2.35946536;
        }


    }

    @Getter
    @Setter
    private static class AltLookAngle {
        private double x;
        private double y;
        private double z;

        private AltLookAngle() {
            this.x = 0;
            this.y = 0;
            this.z = 0;
        }
    }

}
