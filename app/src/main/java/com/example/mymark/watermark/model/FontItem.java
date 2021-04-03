package com.example.mymark.watermark.model;

import android.graphics.Bitmap;

public class FontItem {


    private long id;
    private Bitmap icon;
    private String fontFileLocalPath;
    private String fontFileUrl;
    /**
     * 字距离字框顶部的距离
     */
    private double ascent;

    /**
     * 字距离字框底部的距离
     */
    private double descent;
    /**
     * 字体文件md5
     */
    private String md5;

    public FontItem() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public String getFontFileLocalPath() {
        return fontFileLocalPath;
    }

    public void setFontFileLocalPath(String fontFileLocalPath) {
        this.fontFileLocalPath = fontFileLocalPath;
    }

    public String getFontFileUrl() {
        return fontFileUrl;
    }

    public void setFontFileUrl(String fontFileUrl) {
        this.fontFileUrl = fontFileUrl;
    }

    public double getAscent() {
        return ascent;
    }

    public void setAscent(double ascent) {
        this.ascent = ascent;
    }

    public double getDescent() {
        return descent;
    }

    public void setDescent(double descent) {
        this.descent = descent;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
