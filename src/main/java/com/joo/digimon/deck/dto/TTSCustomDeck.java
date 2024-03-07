package com.joo.digimon.deck.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TTSCustomDeck {
    public TTSCustomDeck(String faceURL, String backURL) {
        this.faceURL = faceURL;
        this.backURL = backURL;
        this.numWidth = 1;
        this.numHeight = 1;
        this.backIsHidden = true;
        this.uniqueBack = false;
        this.type = 0;
    }

    @JsonProperty("FaceURL")
    private String faceURL;
    @JsonProperty("BackURL")
    private String backURL;
    @JsonProperty("NumWidth")
    private int numWidth;
    @JsonProperty("NumHeight")
    private int numHeight;
    @JsonProperty("BackIsHidden")
    private boolean backIsHidden;
    @JsonProperty("UniqueBack")
    private boolean uniqueBack;
    @JsonProperty("Type")
    private int type;

}
