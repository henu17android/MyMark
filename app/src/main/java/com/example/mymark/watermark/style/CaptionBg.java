package com.example.mymark.watermark.style;

import java.util.List;

public class CaptionBg {

    /**
     * 以字幕前景文字高度为基准
     */
    private int baseHeight;

    /**
     * 贴图对象的列表
     */
    private List<CaptionBgSticker> stickerList;

    public int getBaseHeight() {
        return baseHeight;
    }

    public void setBaseHeight(int baseHeight) {
        this.baseHeight = baseHeight;
    }

    public List<CaptionBgSticker> getStickerList() {
        return stickerList;
    }

    public void setStickerList(List<CaptionBgSticker> stickerList) {
        this.stickerList = stickerList;
    }
}
