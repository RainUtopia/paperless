package com.pa.paperless.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mogujie.tt.protobuf.InterfaceMacro;
import com.pa.paperless.R;
import com.pa.paperless.controller.LoginController;
import com.pa.paperless.listener.CallListener;
import com.wind.myapplication.NativeUtil;

public class LoginActivity extends BaseActivity implements View.OnClickListener, CallListener {

    private EditText mEdtUser;
    private EditText mEdtPwd;
    private Button mBtnLogin;
    private NativeUtil nativeUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initController();
        initView();
    }

    @Override
    protected void initController() {
        mController = new LoginController(getApplication());
        mController.setIModelChangeListener(this);
        nativeUtil = NativeUtil.getInstance();
        nativeUtil.setCallListener(this);
//        nativeUtil = new NativeUtil(mController);
    }

    private void initView() {
        mEdtUser = (EditText) findViewById(R.id.edt_user);
        mEdtPwd = (EditText) findViewById(R.id.edt_pwd);
        mBtnLogin = (Button) findViewById(R.id.btn_login);

        mBtnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                submit();
                break;
        }
    }

    private void submit() {
        // validate
        String user = mEdtUser.getText().toString().trim();
        if (TextUtils.isEmpty(user)) {
            Toast.makeText(this, "输入账号", Toast.LENGTH_SHORT).show();
            return;
        }

        String pwd = mEdtPwd.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            Toast.makeText(this, "输入密码", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO validate success, do something
        nativeUtil.adminLogin(InterfaceMacro.Pb_String_LenLimit.Pb_MEET_MD5_ASCILL_MAXLEN.getNumber(),user,pwd);

    }

    @Override
    public void callListener(int action, Object result) {

    }
}
