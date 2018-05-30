package com.pa.paperless;

import android.app.Application;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;

import com.pa.paperless.activity.MainActivity;
import com.pa.paperless.utils.ScreenUtils;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.MoveType;
import com.yhao.floatwindow.PermissionListener;
import com.yhao.floatwindow.Screen;
import com.yhao.floatwindow.ViewStateListener;

/**
 * Created by Administrator on 2018/5/29.
 */

public class MyApplication extends Application {

    private final String TAG = "FloatWindow-->";

    @Override
    public void onCreate() {
        super.onCreate();

//        initImg();
//        initFab();
//        initScreen();//初始化截图批注
    }

    private void initImg() {
        final ImageView img = new ImageView(getApplicationContext());
        img.setImageResource(R.drawable.side);
        FloatWindow.with(getApplicationContext())
                .setView(img)
                .setWidth(100)                //设置控件宽高
                .setHeight(100)
                .setX(100)                    //设置控件初始位置
                .setY(Screen.height, 0.3f)
                .setDesktopShow(true)         //桌面显示
                .setViewStateListener(stateListener)   //监听悬浮控件状态改变
                .setPermissionListener(permissionListener)  //监听权限申请结果
                .setMoveType(MoveType.slide)
                .setMoveStyle(500, new AccelerateInterpolator())  //贴边动画时长为500ms，加速插值器
                .setFilter(false, MainActivity.class)   //MainActivity 页面设置不显示
                .build();
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatWindow.get().hide();
                initFab();
                FloatWindow.get("fab").show();
            }
        });
    }

    private void initFab() {
        FloatWindow.with(getApplicationContext())
                .setView(R.layout.activity_fab)
                .setX(ScreenUtils.getScreenWidth(getApplicationContext()) / 3)
                .setY(ScreenUtils.getScreenHeight(getApplicationContext()) / 3)
                .setDesktopShow(true)   //允许桌面显示
                .setMoveType(MoveType.inactive) //不可拖动
                .setViewStateListener(FabStateListener) //监听状态改变
                .setFilter(false, MainActivity.class)    //主页面不显示
                .setTag("fab")
                .build();
        FloatWindow.get("fab").getView().findViewById(R.id.keyboard).setOnClickListener(FabListenet);
        FloatWindow.get("fab").getView().findViewById(R.id.handwritten).setOnClickListener(FabListenet);
        FloatWindow.get("fab").getView().findViewById(R.id.screens).setOnClickListener(FabListenet);
        FloatWindow.get("fab").getView().findViewById(R.id.one_screen).setOnClickListener(FabListenet);
        FloatWindow.get("fab").getView().findViewById(R.id.back_pop).setOnClickListener(FabListenet);
        FloatWindow.get("fab").getView().findViewById(R.id.projection).setOnClickListener(FabListenet);
        FloatWindow.get("fab").getView().findViewById(R.id.note).setOnClickListener(FabListenet);
        FloatWindow.get("fab").getView().findViewById(R.id.call_service).setOnClickListener(FabListenet);
        FloatWindow.get("fab").getView().findViewById(R.id.whiteplate_pop).setOnClickListener(FabListenet);
    }

    private void initScreen() {
        FloatWindow.with(getApplicationContext())
                .setView(R.layout.activity_postil)
                .setWidth(Screen.width)
                .setHeight(Screen.height)
                .setDesktopShow(true)   //允许桌面显示
                .setMoveType(MoveType.inactive) //不可拖动
                .setViewStateListener(ScreenStateListener) //监听状态改变
                .setFilter(false, MainActivity.class)    //主页面不显示
                .setTag("screen")
                .build();
        FloatWindow.get("screen").getView().findViewById(R.id.postil_image).setOnClickListener(ScreenListener);
        FloatWindow.get("screen").getView().findViewById(R.id.postil_save_local).setOnClickListener(ScreenListener);
        FloatWindow.get("screen").getView().findViewById(R.id.postil_save_server).setOnClickListener(ScreenListener);
        FloatWindow.get("screen").getView().findViewById(R.id.postil_pic).setOnClickListener(ScreenListener);
        FloatWindow.get("screen").getView().findViewById(R.id.postil_start_screen).setOnClickListener(ScreenListener);
        FloatWindow.get("screen").getView().findViewById(R.id.postil_stop_screen).setOnClickListener(ScreenListener);
        FloatWindow.get("screen").getView().findViewById(R.id.postil_start_projection).setOnClickListener(ScreenListener);
        FloatWindow.get("screen").getView().findViewById(R.id.postil_stop_projection).setOnClickListener(ScreenListener);
        FloatWindow.get("screen").getView().findViewById(R.id.postil_pre).setOnClickListener(ScreenListener);
        FloatWindow.get("screen").getView().findViewById(R.id.postil_next).setOnClickListener(ScreenListener);
    }

    private View.OnClickListener ScreenListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.postil_save_local:
                    Log.e(TAG, "MyApplication.onClick :  保存本地 --> ");
                    break;
                case R.id.postil_save_server:
                    Log.e(TAG, "MyApplication.onClick :  保存服务器 --> ");
                    break;
                case R.id.postil_pic:
                    Log.e(TAG, "MyApplication.onClick :  图片批注 --> ");
                    break;
                case R.id.postil_start_screen:
                    Log.e(TAG, "MyApplication.onClick :  发起同屏 --> ");
                    break;
                case R.id.postil_stop_screen:
                    Log.e(TAG, "MyApplication.onClick :  结束同屏 --> ");
                    break;
                case R.id.postil_start_projection:
                    Log.e(TAG, "MyApplication.onClick :  发起投影 --> ");
                    break;
                case R.id.postil_stop_projection:
                    Log.e(TAG, "MyApplication.onClick :  结束投影 --> ");
                    break;
                case R.id.postil_pre:
                    Log.e(TAG, "MyApplication.onClick :  上一页 --> ");
                    break;
                case R.id.postil_next:
                    Log.e(TAG, "MyApplication.onClick :  下一页 --> ");
                    break;
            }
        }
    };

    private View.OnClickListener FabListenet = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.keyboard:
                    Log.e(TAG, "MyApplication.onClick :  点击了软键盘 --> ");
                    break;
                case R.id.handwritten:
                    Log.e(TAG, "MyApplication.onClick :  手写 --> ");
                    break;
                case R.id.screens:
                    Log.e(TAG, "MyApplication.onClick :  截图 --> ");
                    if (FloatWindow.get("screen") == null) {
                        initScreen();//初始化截图批注
                        FloatWindow.get("screen").show();
                    }
                    break;
                case R.id.one_screen:
                    Log.e(TAG, "MyApplication.onClick :  同屏 --> ");
                    break;
                case R.id.back_pop:
                    Log.e(TAG, "MyApplication.onClick :  返回 --> ");
                    FloatWindow.get("fab").hide();
                    FloatWindow.get().show();
                    break;
                case R.id.projection:
                    Log.e(TAG, "MyApplication.onClick :  投影 --> ");
                    break;
                case R.id.note:
                    Log.e(TAG, "MyApplication.onClick :  笔记 --> ");
                    break;
                case R.id.call_service:
                    Log.e(TAG, "MyApplication.onClick :  服务 --> ");
                    break;
                case R.id.whiteplate_pop:
                    Log.e(TAG, "MyApplication.onClick :  画板 --> ");
                    break;
            }

        }
    };

    private PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onSuccess() {
            Log.e(TAG, "MyApplication.onSuccess :  权限申请成功 --> ");
        }

        @Override
        public void onFail() {
            Log.e(TAG, "MyApplication.onFail :  权限申请失败 --> ");
        }
    };

    private ViewStateListener stateListener = new ViewStateListener() {
        @Override
        public void onPositionUpdate(int i, int i1) {
            Log.e(TAG, "img.onPositionUpdate :   --> ");
        }

        @Override
        public void onShow() {
            Log.e(TAG, "img.onShow :   --> ");
//            FloatWindow.get("fab").hide();
//            FloatWindow.get("screen").hide();
        }

        @Override
        public void onHide() {
            Log.e(TAG, "img.onHide :   --> ");
            FloatWindow.destroy();
        }

        @Override
        public void onDismiss() {
            Log.e(TAG, "img.onDismiss :   --> ");
        }

        @Override
        public void onMoveAnimStart() {
            Log.e(TAG, "img.onMoveAnimStart :   --> ");
        }

        @Override
        public void onMoveAnimEnd() {
            Log.e(TAG, "img.onMoveAnimEnd :   --> ");

        }

        @Override
        public void onBackToDesktop() {
            Log.e(TAG, "img.onBackToDesktop :   --> ");
        }
    };

    private ViewStateListener FabStateListener = new ViewStateListener() {
        @Override
        public void onPositionUpdate(int i, int i1) {
            Log.e(TAG, "FabStateListener.onPositionUpdate :   --> ");
        }

        @Override
        public void onShow() {
            Log.e(TAG, "FabStateListener.onShow :   --> ");
//            FloatWindow.get("screen").hide();
        }

        @Override
        public void onHide() {
            Log.e(TAG, "FabStateListener.onHide :   --> ");
        }

        @Override
        public void onDismiss() {
            Log.e(TAG, "FabStateListener.onDismiss :   --> ");
        }

        @Override
        public void onMoveAnimStart() {
            Log.e(TAG, "FabStateListener.onMoveAnimStart :   --> ");
        }

        @Override
        public void onMoveAnimEnd() {
            Log.e(TAG, "FabStateListener.onMoveAnimEnd :   --> ");
        }

        @Override
        public void onBackToDesktop() {
            Log.e(TAG, "FabStateListener.onBackToDesktop :   --> ");
        }
    };

    private ViewStateListener ScreenStateListener = new ViewStateListener() {
        @Override
        public void onPositionUpdate(int i, int i1) {

        }

        @Override
        public void onShow() {
            Log.e(TAG, "ScreenStateListener.onShow :   --> ");
        }

        @Override
        public void onHide() {
            Log.e(TAG, "ScreenStateListener.onHide :   --> ");

        }

        @Override
        public void onDismiss() {
            Log.e(TAG, "ScreenStateListener.onDismiss :   --> ");
        }

        @Override
        public void onMoveAnimStart() {
            Log.e(TAG, "MyApplication.onMoveAnimStart :  ScreenStateListener --> ");
        }

        @Override
        public void onMoveAnimEnd() {
            Log.e(TAG, "MyApplication.onMoveAnimEnd :  ScreenStateListener --> ");
        }

        @Override
        public void onBackToDesktop() {
            Log.e(TAG, "MyApplication.onBackToDesktop :  ScreenStateListener --> ");
        }
    };
}
