package com.pa.paperless.bean;


import com.pa.paperless.views.PaletteView;

/**
 * Created by Administrator on 2018/3/15.
 */

public class DrawPropertyBean {
    PaletteView.Mode mode;
    int linesize;
    int color;
    //左上角右上角坐标
    float lx;
    float ly;
    float rx;
    float ry;

    public PaletteView.Mode getMode() {
        return mode;
    }

    public void setMode(PaletteView.Mode mode) {
        this.mode = mode;
    }

    public int getLinesize() {
        return linesize;
    }

    public void setLinesize(int linesize) {
        this.linesize = linesize;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public float getLx() {
        return lx;
    }

    public void setLx(float lx) {
        this.lx = lx;
    }

    public float getLy() {
        return ly;
    }

    public void setLy(float ly) {
        this.ly = ly;
    }

    public float getRx() {
        return rx;
    }

    public void setRx(float rx) {
        this.rx = rx;
    }

    public float getRy() {
        return ry;
    }

    public void setRy(float ry) {
        this.ry = ry;
    }

    public DrawPropertyBean(PaletteView.Mode mode, int linesize, int color, float lx, float ly, float rx, float ry) {

        this.mode = mode;
        this.linesize = linesize;
        this.color = color;
        this.lx = lx;
        this.ly = ly;
        this.rx = rx;
        this.ry = ry;
    }
}
