package com.example.mymark.watermark.style;

public class CaptionStickerLayer {

    private CaptionBgSticker sticker;

    private int x;
    private int y;
    private int width;
    private int height;

    public CaptionStickerLayer(CaptionBgSticker sticker) {
        this.sticker = sticker;
    }

    public CaptionBgSticker getSticker() {
        return sticker;
    }

    public void setSticker(CaptionBgSticker sticker) {
        this.sticker = sticker;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
