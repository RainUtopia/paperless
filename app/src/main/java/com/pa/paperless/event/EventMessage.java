package com.pa.paperless.event;

/**
 * Created by Administrator on 2018/1/24.
 */

public class EventMessage {
    int action;
    int type;
    Object object;
    public EventMessage(int action) {
        this.action = action;
    }

    public EventMessage(int action, int type) {
        this.action = action;
        this.type = type;
    }

    public EventMessage(int action, Object object) {
        this.action = action;
        this.object = object;
    }

    public EventMessage(int action, int type, Object object) {
        this.action = action;
        this.type = type;
        this.object = object;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
