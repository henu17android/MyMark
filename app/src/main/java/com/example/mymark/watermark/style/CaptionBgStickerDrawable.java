package com.example.mymark.watermark.style;

public class CaptionBgStickerDrawable {

    private String file;
    /**
     * 0: not tile
     * 1: scale
     * 2: repeat
     */
    private int tileMode;
    private double tileStart;
    private double tileEnd;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public int getTileMode() {
        return tileMode;
    }

    public void setTileMode(int tileMode) {
        this.tileMode = tileMode;
    }

    public double getTileStart() {
        return tileStart;
    }

    public void setTileStart(double tileStart) {
        this.tileStart = tileStart;
    }

    public double getTileEnd() {
        return tileEnd;
    }

    public void setTileEnd(double tileEnd) {
        this.tileEnd = tileEnd;
    }
}
