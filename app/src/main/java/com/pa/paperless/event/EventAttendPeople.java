package com.pa.paperless.event;

/**
 * Created by Administrator on 2018/1/16.
 */

public class EventAttendPeople
{
    int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public EventAttendPeople(int id) {

        this.id = id;
    }
}
