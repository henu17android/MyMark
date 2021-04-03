package com.example.mymark.watermark.style;

import java.util.List;

public class CaptionFgHighlightText {

    /**
     * 高亮的类型
     * 1: 随机
     * 2: 指定位置
     */
    private int type;
    /**
     * 高亮字的颜色
     */
    private String color;
    /**
     * 高亮字出现的概率
     * 当高亮类型为1时生效
     */
    private float random;
    /**
     * 高亮字在句子中的位置, 1代表正数第一个字，-1代表倒数第一个字
     * 当高亮类型为2时生效
     */
    private List<Integer> index;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public float getRandom() {
        return random;
    }

    public void setRandom(float random) {
        this.random = random;
    }

    public List getIndex() {
        return index;
    }

    public void setIndex(List index) {
        this.index = index;
    }
}
