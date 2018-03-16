package com.pa.paperless.activity;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.pa.paperless.R;
import com.yinghe.whiteboardlib.fragment.WhiteBoardFragment;

public class WhiteBoardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_white_board);
        FragmentTransaction ts = getSupportFragmentManager().beginTransaction();
        WhiteBoardFragment whiteBoardFragment = WhiteBoardFragment.newInstance();
        ts.add(R.id.fl_board, whiteBoardFragment, "wb").commit();
    }
}
