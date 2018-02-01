package com.pa.paperless.fragment.manage.secondfloor.s.prepare;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.pa.paperless.R;
import com.pa.paperless.fragment.meeting.BaseFragment;
import com.zhy.android.percent.support.PercentLinearLayout;

/**
 * Created by Administrator on 2017/11/17.
 * 会前准备-桌牌显示
 */

public class TabletShow_Fragment extends BaseFragment implements View.OnClickListener {
    private Spinner mFirstlineSpinner;
    private Button mFirstlineTextcolorBtn;
    private EditText mFirstlineTextsizeEdt;
    private Spinner mFirstlineFonttypeSpinner;
    private Spinner mFirstlineTextboldSpinner;
    private Spinner mSecondlineSpinner;
    private Button mSecondlineTextcolorBtn;
    private EditText mSecondlineTextsizeEdt;
    private Spinner mSecondlineFonttypeSpinner;
    private Spinner mSecondlineTextboldSpinner;
    private Spinner mThirdlineSpinner;
    private Button mThirdlineTextcolorBtn;
    private EditText mThirdlineTextsizeEdt;
    private Spinner mThirdlineFonttypeSpinner;
    private Spinner mThirdlineTextboldSpinner;
    private PercentLinearLayout mPlace1;
    private Button mSaveSetting;
    private PercentLinearLayout mPlace2;
    private RecyclerView mTableShowRl;
    private Button mChooseBagPicture;
    private Button mImportBagPicture;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.tablet_show, container, false);
        initView(inflate);
        return inflate;
    }

    private void initView(View inflate) {
        mFirstlineSpinner = (Spinner) inflate.findViewById(R.id.firstline_spinner);
        mFirstlineTextcolorBtn = (Button) inflate.findViewById(R.id.firstline_textcolor_btn);
        mFirstlineTextsizeEdt = (EditText) inflate.findViewById(R.id.firstline_textsize_edt);
        mFirstlineFonttypeSpinner = (Spinner) inflate.findViewById(R.id.firstline_fonttype_spinner);
        mFirstlineTextboldSpinner = (Spinner) inflate.findViewById(R.id.firstline_textbold_spinner);
        mSecondlineSpinner = (Spinner) inflate.findViewById(R.id.secondline_spinner);
        mSecondlineTextcolorBtn = (Button) inflate.findViewById(R.id.secondline_textcolor_btn);
        mSecondlineTextsizeEdt = (EditText) inflate.findViewById(R.id.secondline_textsize_edt);
        mSecondlineFonttypeSpinner = (Spinner) inflate.findViewById(R.id.secondline_fonttype_spinner);
        mSecondlineTextboldSpinner = (Spinner) inflate.findViewById(R.id.secondline_textbold_spinner);
        mThirdlineSpinner = (Spinner) inflate.findViewById(R.id.thirdline_spinner);
        mThirdlineTextcolorBtn = (Button) inflate.findViewById(R.id.thirdline_textcolor_btn);
        mThirdlineTextsizeEdt = (EditText) inflate.findViewById(R.id.thirdline_textsize_edt);
        mThirdlineFonttypeSpinner = (Spinner) inflate.findViewById(R.id.thirdline_fonttype_spinner);
        mThirdlineTextboldSpinner = (Spinner) inflate.findViewById(R.id.thirdline_textbold_spinner);
        mPlace1 = (PercentLinearLayout) inflate.findViewById(R.id.place1);
        mSaveSetting = (Button) inflate.findViewById(R.id.save_setting);
        mPlace2 = (PercentLinearLayout) inflate.findViewById(R.id.place2);
        mTableShowRl = (RecyclerView) inflate.findViewById(R.id.table_show_rl);
        mChooseBagPicture = (Button) inflate.findViewById(R.id.choose_bag_picture);
        mImportBagPicture = (Button) inflate.findViewById(R.id.import_bag_picture);

        mFirstlineTextcolorBtn.setOnClickListener(this);
        mSecondlineTextcolorBtn.setOnClickListener(this);
        mThirdlineTextcolorBtn.setOnClickListener(this);
        mSaveSetting.setOnClickListener(this);
        mChooseBagPicture.setOnClickListener(this);
        mImportBagPicture.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.firstline_textcolor_btn:

                break;
            case R.id.secondline_textcolor_btn:

                break;
            case R.id.thirdline_textcolor_btn:

                break;
            case R.id.save_setting:

                break;
            case R.id.choose_bag_picture:

                break;
            case R.id.import_bag_picture:

                break;
        }
    }
}