package com.pa.paperless.bean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2018/1/22.
 */

public class CheckedMemberIds  /*implements Iterable<Integer>*/{
    int Checkid;

    public int getCheckid() {
        return Checkid;
    }

    public void setCheckid(int checkid) {
        Checkid = checkid;
    }

    public CheckedMemberIds(int checkid) {

        Checkid = checkid;
    }
//
//    @Override
//    public Iterator<Integer> iterator() {
//        List<Integer> ids = new ArrayList<>();
//        ids.add(Checkid);
//        Iterator<Integer> iterator = ids.iterator();
//        return iterator;
//    }
}
