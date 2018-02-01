package com.pa.paperless.bean;

/**
 * Created by Administrator on 2017/11/22.
 */

public class VenueBean {
    String name;
    String place;

    public VenueBean(String name, String place) {
        this.name = name;
        this.place = place;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }
}
