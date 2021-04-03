package com.example.mymark.watermark.style;

import android.text.TextPaint;

public class CaptionStrokeLayer {

    public TextPaint strokePaint;
    public CaptionFgStrokeLayer strokeParams;

    public CaptionStrokeLayer(CaptionFgStrokeLayer params) {
        strokePaint = new TextPaint();
        strokeParams = new CaptionFgStrokeLayer(params.getStrokeWidth(), params.getDx(), params.getDy(), params.getTextColor());
    }
}
