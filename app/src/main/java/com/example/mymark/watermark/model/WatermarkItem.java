package com.example.mymark.watermark.model;

import android.graphics.Bitmap;

import com.example.mymark.watermark.style.CaptionFg;

public class WatermarkItem {
    private String version;
    private long id; // string.valueof(id) as folder name
    private String name;// waterMark Id
    private boolean vip;
    private boolean hide;
    private boolean editable;
    private boolean singleLine = true;
    private CaptionFg foreground;
    private CaptionBg background;

    private Bitmap icon;


    public String getVersion() {
        return version;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isVip() {
        return vip;
    }

    public boolean isHide() {
        return hide;
    }

    public boolean isEditable() {
        return editable;
    }

    public boolean isSingleLine() {
        return singleLine;
    }

    public CaptionFg getForeground() {
        return foreground;
    }

    public CaptionBg getBackground() {
        return background;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVip(boolean vip) {
        this.vip = vip;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public void setSingleLine(boolean singleLine) {
        this.singleLine = singleLine;
    }

    public void setForeground(CaptionFg foreground) {
        this.foreground = foreground;
    }

    public void setBackground(CaptionBg background) {
        this.background = background;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }


    @Override
    public int compareTo(WatermarkItem o) {
        return Long.compare(this.getId(), o.getId());
    }
}
