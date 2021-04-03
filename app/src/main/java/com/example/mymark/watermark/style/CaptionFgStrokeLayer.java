package com.example.mymark.watermark.style;

public class CaptionFgStrokeLayer {

    private String textColor;
    private float strokeWidth;
    private float dx;
    private float dy;

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public float getDx() {
        return dx;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    public float getDy() {
        return dy;
    }

    public void setDy(int dy) {
        this.dy = dy;
    }

    public CaptionFgStrokeLayer(float strokeWidth, float dx, float dy, String textColor) {
        this.strokeWidth = strokeWidth;
        this.dx = dx;
        this.dy = dy;
        this.textColor = textColor;
    }
}
