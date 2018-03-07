package com.pa.paperless.fragment.manage.secondfloor.s.setting;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceMain;
import com.pa.paperless.R;
import com.pa.paperless.constant.IDEventMessage;
import com.pa.paperless.constant.IDivMessage;
import com.pa.paperless.event.EventMessage;
import com.pa.paperless.fragment.meeting.BaseFragment;
import com.pa.paperless.listener.CallListener;
import com.wind.myapplication.NativeUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/16.
 * 系统设置-其它设置
 */

public class OtherSettingFragment extends BaseFragment implements View.OnClickListener, CallListener {

    private NativeUtil nativeUtil;
    private EditText mOldPassword;
    private EditText mNewPassword;
    private Button mSubmitBtn;
//    private NativeUtil nativeUtil;
    private EditText mAlginPassword;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case IDivMessage.QUERY_ADMIN_INFO://查询管理员
                    ArrayList queryAdmin = msg.getData().getParcelableArrayList("queryAdmin");
                    InterfaceMain.pbui_TypeAdminDetailInfo o = (InterfaceMain.pbui_TypeAdminDetailInfo) queryAdmin.get(0);
                    List<InterfaceMain.pbui_Item_AdminDetailInfo> itemList = o.getItemList();

                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.other_setting, container, false);
        initController();
        initView(inflate);
        try {
            //52.查询管理员
            nativeUtil.queryAdmin();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        EventBus.getDefault().register(this);
        return inflate;
    }

    public void getEventMessage(EventMessage message) throws InvalidProtocolBufferException {
        switch (message.getAction()){
            case IDEventMessage.ADMIN_NOTIFI_INFORM:
                nativeUtil.queryAdmin();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initController() {
        nativeUtil = NativeUtil.getInstance();
//        nativeUtil = new NativeUtil();
        nativeUtil.setCallListener(this);
    }

    private void initView(View inflate) {
        mOldPassword = (EditText) inflate.findViewById(R.id.old_password);
        mNewPassword = (EditText) inflate.findViewById(R.id.new_password);
        mAlginPassword = (EditText) inflate.findViewById(R.id.algin_password);
        mSubmitBtn = (Button) inflate.findViewById(R.id.submit_btn);

        mSubmitBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit_btn:
                //1.判断输入的原密码是否正确
                String getOldPwd = mOldPassword.getText().toString().trim();
                String getAlginPwd = mAlginPassword.getText().toString().trim();
                String getNewPassword = mNewPassword.getText().toString().trim();
                if (getOldPwd.equals(getAlginPwd)) {
                    //54.修改管理员自身的密码
                    nativeUtil.modifAdminPw("", getOldPwd, getNewPassword);
                } else {
                    Toast.makeText(getActivity(), "两次输入的密码不一致~", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void callListener(int action, Object result) {
        switch (action){
            case IDivMessage.QUERY_ADMIN_INFO:
                InterfaceMain.pbui_TypeAdminDetailInfo result1 = (InterfaceMain.pbui_TypeAdminDetailInfo) result;
                if (result1 != null) {
                    Bundle bundle = new Bundle();
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(result1);
                    bundle.putParcelableArrayList("queryAdmin", arrayList);
                    Message message = new Message();
                    message.what = action;
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
                break;
        }
    }
}
