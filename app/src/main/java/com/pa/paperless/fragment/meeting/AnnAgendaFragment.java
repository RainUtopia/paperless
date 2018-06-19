package com.pa.paperless.fragment.meeting;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.pa.paperless.R;

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

    @Override
    public void onAttach(Context context) {
        Log.i("F_life", "AnnAgendaFragment.onAttach :   --> ");
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i("F_life", "AnnAgendaFragment.onCreate :   --> ");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.i("F_life", "AnnAgendaFragment.onActivityCreated :   --> ");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        Log.i("F_life", "AnnAgendaFragment.onStart :   --> ");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.i("F_life", "AnnAgendaFragment.onResume :   --> ");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.i("F_life", "AnnAgendaFragment.onPause :   --> ");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i("F_life", "AnnAgendaFragment.onStop :   --> ");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.i("F_life", "AnnAgendaFragment.onDestroyView :   --> ");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.i("F_life", "AnnAgendaFragment.onDestroy :   --> ");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.i("F_life", "AnnAgendaFragment.onDetach :   --> ");
        super.onDetach();
    }
}
