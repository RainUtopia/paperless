package com.pa.paperless.event;

/**
 * Created by Administrator on 2018/1/16.
 *  Event定义消息事件类
 */

public class MessageEvent {
    private String[] message;

    public MessageEvent(String[] message) {
        this.message = message;
    }

    public String[] getMessage() {
        return message;
    }

    public void setMessage(String[] message) {
        this.message = message;
    }
}
