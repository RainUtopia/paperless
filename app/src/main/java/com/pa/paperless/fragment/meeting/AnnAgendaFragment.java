package com.pa.paperless.fragment.meeting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.pa.paperless.R;
import com.pa.paperless.listener.CallListener;
import com.wind.myapplication.NativeUtil;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/10/31.
 * 公告议程
 */

public class AnnAgendaFragment extends BaseFragment implements View.OnClickListener {

    private ImageView mAgendaImgbtn;
    private ImageView mAnnouncementImgbtn;
    private FrameLayout mAgendaLl;
    private ArrayList<BaseFragment> mFragments;
    private ArrayList<ImageView> mImgbtns;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.right_announce_agenda, container, false);
        initView(inflate);
        initImageBtns();
        initFragments();
        changeFragment(mFragments.get(0));
        setImgSelect(0);
        return inflate;
    }


    private void initImageBtns() {
        mImgbtns = new ArrayList<>();
        mImgbtns.add(mAgendaImgbtn);
        mImgbtns.add(mAnnouncementImgbtn);
    }

    private void setImgSelect(int index) {
        for (int i = 0; i < mImgbtns.size(); i++) {
            if (i == index) {
                mImgbtns.get(i).setSelected(true);
            } else {
                mImgbtns.get(i).setSelected(false);
            }
        }
    }

    private void changeFragment(BaseFragment fragment) {
        FragmentManager fManager = getChildFragmentManager();
        FragmentTransaction transaction = fManager.beginTransaction();
        transaction.replace(R.id.agenda_fl, fragment);
        transaction.commitAllowingStateLoss();
    }

    private void initFragments() {
        mFragments = new ArrayList<>();
        mFragments.add(new AgendaFragment());
        mFragments.add(new NoticeFragment());
    }

    private void initView(View inflate) {
        mAgendaImgbtn = (ImageView) inflate.findViewById(R.id.agenda_imgbtn);
        mAnnouncementImgbtn = (ImageView) inflate.findViewById(R.id.announcement_imgbtn);
        mAgendaLl = (FrameLayout) inflate.findViewById(R.id.agenda_fl);

        mAgendaImgbtn.setOnClickListener(this);
        mAnnouncementImgbtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.agenda_imgbtn:
                changeFragment(mFragments.get(0));
                setImgSelect(0);
                break;
            case R.id.announcement_imgbtn:
                changeFragment(mFragments.get(1));
                setImgSelect(1);
                break;
        }
    }
}
