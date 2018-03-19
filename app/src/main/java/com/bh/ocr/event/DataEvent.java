package com.bh.ocr.event;

/**
 * EventBus 自定义事件
 */

public class DataEvent {
    private String img;
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getImg() {
        return img;
    }
    public void setImg(String img) {
        this.img = img;
    }
}
