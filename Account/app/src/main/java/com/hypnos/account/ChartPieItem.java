package com.hypnos.account;

public class ChartPieItem {

    String tag;
    String price;
    int color;

    public ChartPieItem(String tag, String price, int color) {
        this.tag = tag;
        this.price = price;
        this.color = color;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
