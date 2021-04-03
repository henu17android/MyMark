package com.example.mymark.watermark.style;

public class CaptionBgStickerLayoutWidth {

    /**
     * 0：宽度=value
     * 1：宽度=字幕宽度+value
     */
    private int type;
    private int value;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
