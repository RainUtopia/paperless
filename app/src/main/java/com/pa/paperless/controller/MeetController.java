package com.pa.paperless.controller;

import android.content.Context;

import com.pa.paperless.constant.IDivMessage;


/**
 * Created by Administrator on 2018/1/5.
 */

public class MeetController extends BaseController {


    public MeetController(Context c) {
        super(c);
    }

    @Override
    protected void handleMessage(int action, Object[] values) {
        switch (action) {

            case IDivMessage.GET_AGEND:
                if (mListener != null) {

                }
        }
    }


}
