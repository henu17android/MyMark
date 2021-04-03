package com.example.mymark.watermark.style;

import java.util.List;

public class CaptionFg {

    private String textColor;
    private long fontId = -1;
    private String fontSize;
    private String bitmapShader;
    private CaptionFgShadow shadow;
    private List<CaptionFgStrokeLayer> strokeLayers;
    private CaptionFgHighlightText highlightText;

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public String getBitmapShader() {
        return bitmapShader;
    }

    public void setBitmapShader(String bitmapShader) {
        this.bitmapShader = bitmapShader;
    }

    public CaptionFgShadow getShadow() {
        return shadow;
    }

//    public void setShadow(CaptionFgShadow shadow) {
//        this.shadow = shadow;
//    }

    public List<CaptionFgStrokeLayer> getStrokeLayers() {
        return strokeLayers;
    }

    public void setStrokeLayers(List<CaptionFgStrokeLayer> strokeLayers) {
        this.strokeLayers = strokeLayers;
    }

    public long getFontId() {
        return fontId;
    }

    public void setFontId(long fontId) {
        this.fontId = fontId;
    }

    public CaptionFgHighlightText getHighlightText() {
        return highlightText;
    }

    public void setHighlightText(CaptionFgHighlightText highlightText) {
        this.highlightText = highlightText;
    }

    public String getFontSize() {
        return fontSize;
    }

    public void setFontSize(String fontSize) {
        this.fontSize = fontSize;
    }
}
