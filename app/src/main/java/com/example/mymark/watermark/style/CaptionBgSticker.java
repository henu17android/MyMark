package com.example.mymark.watermark.style;

public class CaptionBgSticker {

    /**
     * 是否盖住文字
     */
    private boolean aboveFg;

    /**
     * 贴图对象文件和拉伸属性
     */
    private CaptionBgStickerDrawable drawable;

    /**
     * 贴图对象的高宽属性
     */
    private CaptionBgStickerLayout layout;

    /**
     * 贴图对象的约束属性
     */
    private CaptionBgStickerConstraints constraints;

    public CaptionBgStickerDrawable getDrawable() {
        return drawable;
    }

    public void setDrawable(CaptionBgStickerDrawable drawable) {
        this.drawable = drawable;
    }

    public CaptionBgStickerLayout getLayout() {
        return layout;
    }

    public void setLayout(CaptionBgStickerLayout layout) {
        this.layout = layout;
    }

    public CaptionBgStickerConstraints getConstraints() {
        return constraints;
    }

    public void setConstraints(CaptionBgStickerConstraints constraints) {
        this.constraints = constraints;
    }

    public boolean isAboveFg() {
        return aboveFg;
    }

    public void setAboveFg(boolean aboveFg) {
        this.aboveFg = aboveFg;
    }
}
