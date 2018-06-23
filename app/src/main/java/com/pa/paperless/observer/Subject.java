package com.pa.paperless.observer;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/6/22.
 */

public class Subject {
    private final String TAG = "Observer-->";
    private List<Observer> observers = new ArrayList<>();

    public void inform(int action, int type) {
        Log.d(TAG, "inform: " + action + " : " + type);
        notifyAllObserver(action, type);
    }

    public void attach(Observer o) {
        observers.add(o);
    }

    public void notifyAllObserver(int action, int type) {
        for (Observer o : observers) {
            o.updata(action, type);
        }
    }
}
