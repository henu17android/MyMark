package com.example.mymark.watermark.style;

public class CaptionBgStickerConstraints {

    // value: top or bottom
    private String top;
    // value: top or bottom
    private String bottom;
    // value: left or right
    private String left;
    // value: left or right
    private String right;
    // h & v bias
    private CaptionBgStickerConstraintsBias bias;

    public String getTop() {
        return top;
    }

    public void setTop(String top) {
        this.top = top;
    }

    public String getBottom() {
        return bottom;
    }

    public void setBottom(String bottom) {
        this.bottom = bottom;
    }

    public String getLeft() {
        return left;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public String getRight() {
        return right;
    }

    public void setRight(String right) {
        this.right = right;
    }

    public CaptionBgStickerConstraintsBias getBias() {
        return bias;
    }

    public void setBias(CaptionBgStickerConstraintsBias bias) {
        this.bias = bias;
    }
}
