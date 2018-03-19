package com.pa.paperless.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.mogujie.tt.protobuf.InterfaceMacro;
import com.pa.paperless.R;
import com.pa.paperless.bean.DrawPropertyBean;
import com.pa.paperless.constant.IDivMessage;
import com.pa.paperless.listener.CallListener;
import com.pa.paperless.utils.GetPicFilePathUtil;
import com.pa.paperless.utils.ImageUtils;
import com.pa.paperless.views.ColorPickerDialog;
import com.pa.paperless.views.PaletteView;
import com.wind.myapplication.NativeUtil;

import java.util.ArrayList;
import java.util.List;

import static com.pa.paperless.utils.MyUtils.setAnimator;


public class DrawBoardActivity extends BaseActivity implements View.OnClickListener {

    public static int PHOTO_REQUEST_GALLERY = 1;
    private ImageView mPen;
    private ImageView mEraser;
    private ImageView mPalette;
    private ImageView mText;
    private ImageView mPic;
    private ImageView mSline;
    private ImageView mCurve;
    private ImageView mEllpse;
    private ImageView mRect;
    private ImageView mBack;
    private ImageView mClean;
    private SeekBar mSeb;
    private int mPaintWidth = 10; // 设置画笔的默认宽度是 10
    private ImageView mColorBlack;
    private ImageView mColorGray;
    private ImageView mColorRed;
    private ImageView mColorOrange;
    private ImageView mColorYellow;
    private ImageView mColorBrown;
    private ImageView mColorPurple;
    private ImageView mColorBlue;
    private ImageView mColorCyan;
    private ImageView mColorGreen;
    private Button mShareStart;
    private Button mShareStop;
    private Button mSave;
    private Button mExit;
    private PaletteView mPaletteView; //自定义的画板

    private List<ImageView> mTopImages;//上方的图片
    private List<ImageView> mBotImages;//下方的图片
    private NativeUtil nativeUtil;
    //获取绘画时的参数
    private DrawPropertyBean drawPropertyBean;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_board);
        initController();
        initView();
        initImages();
        initDefaultColor();
        initEvent();
    }

    @Override
    protected void initController() {
        nativeUtil = NativeUtil.getInstance();
    }

    private void initEvent() {
        /**
         * 监听 SeekBar 的值
         */
        mSeb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            /**
             * 停止
             * @param seekBar
             */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //将停下时的值 赋给 mPaintWidth 达到控制画笔大小
                // TODO: 2017/11/6
                mPaintWidth = seekBar.getProgress();
                mPaletteView.setPenRawSize(mPaintWidth);
                mPaletteView.setEraserSize(mPaintWidth);
            }
        });

        mPaletteView.setListener(new CallListener() {
            @Override
            public void callListener(int action, Object result) {
                if (action == IDivMessage.GET_DRAW_INFO) {
                    Log.e("MyLog", "DrawBoardActivity.callListener 124行:   --->>> ");
                    long timeMillis = System.currentTimeMillis();
                    drawPropertyBean = (DrawPropertyBean) result;
                    switch (drawPropertyBean.getMode()) {
                        case CIRCLE:
                            addForm(count++, timeMillis, InterfaceMacro.Pb_MeetPostilFigureType.Pb_WB_FIGURETYPE_ELLIPSE.getNumber());
                            break;
                        case LINE:
                            addForm(count++, timeMillis, InterfaceMacro.Pb_MeetPostilFigureType.Pb_WB_FIGURETYPE_LINE.getNumber());
                            break;
                        case DRAW:
                            addForm(count++, timeMillis, InterfaceMacro.Pb_MeetPostilFigureType.Pb_WB_FIGURETYPE_INK.getNumber());
                            break;
                        case RECT:
                            addForm(count++, timeMillis, InterfaceMacro.Pb_MeetPostilFigureType.Pb_WB_FIGURETYPE_RECTANGLE.getNumber());
                            break;
                        case TEXT:
                            addForm(count++, timeMillis, InterfaceMacro.Pb_MeetPostilFigureType.Pb_WB_FIGURETYPE_FREETEXT.getNumber());
                            break;
                    }
                }
            }
        });
    }

    private void addForm(int count, long timeMillis, int type) {
        long time = 1000;
        nativeUtil.addDrawFigure(count, MeetingActivity.o.getMemberid(), MeetingActivity.o.getMemberid(), time, timeMillis,
                type, drawPropertyBean.getLinesize(), drawPropertyBean.getColor(), drawPropertyBean.getLx(),
                drawPropertyBean.getLy(), drawPropertyBean.getRx(), drawPropertyBean.getRy());
    }

    /**
     * 设置可快捷选择的颜色
     */
    private void initDefaultColor() {
        setImageColor(mColorBlack, Color.BLACK);
        setImageColor(mColorGray, Color.GRAY);
        setImageColor(mColorRed, Color.RED);
        setImageColor(mColorOrange, Color.rgb(222, 162, 89));
        setImageColor(mColorYellow, Color.YELLOW);
        setImageColor(mColorBrown, Color.rgb(113, 93, 79));
        setImageColor(mColorPurple, Color.rgb(170, 0, 255));
        setImageColor(mColorBlue, Color.BLUE);
        setImageColor(mColorCyan, Color.CYAN);
        setImageColor(mColorGreen, Color.GREEN);
    }

    /**
     * 使用 shape 的同时，利用代码修改 shape 的填充颜色
     *
     * @param view 需要修改的颜色的 View
     * @param argb 需要改成的颜色
     */
    private void setImageColor(ImageView view, int argb) {
        GradientDrawable myGraw = (GradientDrawable) view.getBackground();
        myGraw.setColor(argb);
    }


    /**
     * 设置 图片选中状态的方法
     *
     * @param list
     * @param index
     */
    private void setImgSelect(List<ImageView> list, int index) {
        for (int i = 0; i < list.size(); i++) {
            if (i == index) {
                list.get(i).setSelected(true);
            } else {
                list.get(i).setSelected(false);
            }
        }
    }

    /**
     * 将有选择器的Image放到列表中
     */
    private void initImages() {
        //上方的图片
        mTopImages = new ArrayList<>();
        mTopImages.add(mPen);
        mTopImages.add(mEraser);
        mTopImages.add(mPalette);
        mTopImages.add(mText);
        mTopImages.add(mPic);
        mTopImages.add(mSline);
        mTopImages.add(mCurve);
        mTopImages.add(mEllpse);
        mTopImages.add(mRect);
        mTopImages.add(mBack);
        mTopImages.add(mClean);
        //下方的图片 --> 10个颜色 圆形
        mBotImages = new ArrayList<>();
        mBotImages.add(mColorBlack);
        mBotImages.add(mColorGray);
        mBotImages.add(mColorRed);
        mBotImages.add(mColorOrange);
        mBotImages.add(mColorYellow);
        mBotImages.add(mColorBrown);
        mBotImages.add(mColorPurple);
        mBotImages.add(mColorBlue);
        mBotImages.add(mColorCyan);
        mBotImages.add(mColorGreen);

    }

    private void initView() {
        mPen = (ImageView) findViewById(R.id.pen);
        mEraser = (ImageView) findViewById(R.id.eraser);
        mPalette = (ImageView) findViewById(R.id.palette);
        mText = (ImageView) findViewById(R.id.text);
        mPic = (ImageView) findViewById(R.id.pic);
        mSline = (ImageView) findViewById(R.id.sline);
        mCurve = (ImageView) findViewById(R.id.curve);
        mEllpse = (ImageView) findViewById(R.id.ellpse);
        mRect = (ImageView) findViewById(R.id.rect);
        mBack = (ImageView) findViewById(R.id.back);
        mClean = (ImageView) findViewById(R.id.clean);
        mSeb = (SeekBar) findViewById(R.id.seb);
        mColorBlack = (ImageView) findViewById(R.id.color_black);
        mColorGray = (ImageView) findViewById(R.id.color_gray);
        mColorRed = (ImageView) findViewById(R.id.color_red);
        mColorOrange = (ImageView) findViewById(R.id.color_orange);
        mColorYellow = (ImageView) findViewById(R.id.color_yellow);
        mColorBrown = (ImageView) findViewById(R.id.color_brown);
        mColorPurple = (ImageView) findViewById(R.id.color_purple);
        mColorBlue = (ImageView) findViewById(R.id.color_blue);
        mColorCyan = (ImageView) findViewById(R.id.color_cyan);
        mColorGreen = (ImageView) findViewById(R.id.color_green);
        mShareStart = (Button) findViewById(R.id.share_start);
        mShareStop = (Button) findViewById(R.id.share_stop);
        mSave = (Button) findViewById(R.id.save);
        mExit = (Button) findViewById(R.id.exit);
        mPaletteView = (PaletteView) findViewById(R.id.my_drawingboard);

        mPen.setOnClickListener(this);
        mEraser.setOnClickListener(this);
        mPalette.setOnClickListener(this);
        mText.setOnClickListener(this);
        mPic.setOnClickListener(this);
        mSline.setOnClickListener(this);
        mCurve.setOnClickListener(this);
        mEllpse.setOnClickListener(this);
        mRect.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mClean.setOnClickListener(this);
        mColorBlack.setOnClickListener(this);
        mColorGray.setOnClickListener(this);
        mColorRed.setOnClickListener(this);
        mColorOrange.setOnClickListener(this);
        mColorYellow.setOnClickListener(this);
        mColorBrown.setOnClickListener(this);
        mColorPurple.setOnClickListener(this);
        mColorBlue.setOnClickListener(this);
        mColorCyan.setOnClickListener(this);
        mColorGreen.setOnClickListener(this);
        mShareStart.setOnClickListener(this);
        mShareStop.setOnClickListener(this);
        mSave.setOnClickListener(this);
        mExit.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pen:
                setImgSelect(mTopImages, 0);
                mPaletteView.setMode(PaletteView.Mode.DRAW);
                break;
            case R.id.eraser://橡皮擦
                setImgSelect(mTopImages, 1);
                mPaletteView.setMode(PaletteView.Mode.ERASER);
                break;
            case R.id.palette://调色板
                setImgSelect(mTopImages, 2);
                new ColorPickerDialog(DrawBoardActivity.this, new ColorPickerDialog.OnColorChangedListener() {
                    @Override
                    public void colorChanged(int color) {
                        mPaletteView.setPenColor(color);
                    }
                }, Color.BLACK).show();
                break;
            case R.id.text://添加文字
                setImgSelect(mTopImages, 3);
                /** ************ ******  231.添加文本  ****** ************ **/
//                nativeUtil.addText();
                break;
            case R.id.pic://打开系统相册
                setImgSelect(mTopImages, 4);
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
                startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
                break;
            case R.id.sline: //画直线
                setImgSelect(mTopImages, 5);
                mPaletteView.setMode(PaletteView.Mode.LINE);
                break;
            case R.id.curve://画曲线
                setImgSelect(mTopImages, 6);
                mPaletteView.setMode(PaletteView.Mode.DRAW);
                break;
            case R.id.ellpse://画圆
                setImgSelect(mTopImages, 7);
                mPaletteView.setMode(PaletteView.Mode.CIRCLE);
                break;
            case R.id.rect://矩形
                setImgSelect(mTopImages, 8);
                mPaletteView.setMode(PaletteView.Mode.RECT);
                break;
            case R.id.back://撤销
                setImgSelect(mTopImages, 9);
                mPaletteView.undo();
                break;
            case R.id.clean://清空
                setImgSelect(mTopImages, 10);
                mPaletteView.clear();
                break;
            case R.id.color_black:
                setAnimator(mColorBlack);
                mPaletteView.setPenColor(Color.BLACK);
                break;
            case R.id.color_gray:
                setAnimator(mColorGray);

                mPaletteView.setPenColor(Color.GRAY);
                break;
            case R.id.color_red:
                setAnimator(mColorRed);
                mPaletteView.setPenColor(Color.RED);
                break;
            case R.id.color_orange:
                setAnimator(mColorOrange);
                mPaletteView.setPenColor(Color.rgb(222, 162, 89));
                break;
            case R.id.color_yellow:
                setAnimator(mColorYellow);
                mPaletteView.setPenColor(Color.YELLOW);
                break;
            case R.id.color_brown:
                setAnimator(mColorBrown);
                mPaletteView.setPenColor(Color.rgb(113, 93, 79));
                break;
            case R.id.color_purple:
                setAnimator(mColorPurple);
                mPaletteView.setPenColor(Color.rgb(170, 0, 255));
                break;
            case R.id.color_blue:
                setAnimator(mColorBlue);
                mPaletteView.setPenColor(Color.BLUE);
                break;
            case R.id.color_cyan:
                setAnimator(mColorCyan);
                mPaletteView.setPenColor(Color.CYAN);
                break;
            case R.id.color_green:
                setAnimator(mColorGreen);
                mPaletteView.setPenColor(Color.GREEN);
                break;
            case R.id.share_start:

                break;
            case R.id.share_stop:

                break;
            case R.id.save:
                ImageUtils.saveToGallery(getApplicationContext(), mPaletteView.buildBitmap());
                break;
            case R.id.exit:
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PHOTO_REQUEST_GALLERY) {
            // 从相册返回的数据
            if (data != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
                String choosePic = GetPicFilePathUtil.getFilePathByContentResolver(getApplicationContext(), uri);
                //// TODO: 2017/12/9 将图片展示到Canvas画板中
                //如果选中的图片过大就会报OOM
                Bitmap bitmap = BitmapFactory.decodeFile(choosePic);
                int bitmapWidth = bitmap.getWidth();
                int bitmapHeight = bitmap.getHeight();
                Log.e("MyLog", "DrawBoardActivity.onActivityResult:选中的图片宽高：  bitmapWidth --->>> " + bitmapWidth + "||   bitmapHeight--->>" + bitmapHeight);
                if (mPaletteView.mBufferCanvas == null) {
                    mPaletteView.initBuffer();
                }
                Log.e("MyLog", "DrawBoardActivity.onActivityResult:  mPaletteView.mBufferCanvas!=null --->>> " + (mPaletteView.mBufferCanvas != null));
                int canvasWidth = mPaletteView.getWidth();
//                int canvasHeight = mPaletteView.getHeight();
                int canvasHeight = 1026;
                Log.e("MyLog", "DrawBoardActivity.onActivityResult:  画板的宽高： canvasWidth--->>> " + canvasWidth + "   canvasHeight  -->>" + canvasHeight + "     " + mPaletteView.getHeight());
                int newW = bitmapWidth;
                int newH = bitmapHeight;
                if (bitmapWidth > canvasWidth) {
                    Log.e("MyLog", "DrawBoardActivity.onActivityResult:  图片的宽大于或等于画板的宽 ");
                    newW = canvasWidth;
                }
                if (bitmapHeight > canvasHeight) {
                    Log.e("MyLog", "DrawBoardActivity.onActivityResult:  图片的高大于或等于画板的高");
                    newH = canvasHeight;
                }
                if (bitmapWidth < canvasWidth / 2) {
                    Log.e("MyLog", "DrawBoardActivity.onActivityResult:  图片的宽小于画板的一半 --->>> ");
                    newW = canvasWidth / 2;
                }
                if (bitmapHeight < canvasHeight / 2) {
                    Log.e("MyLog", "DrawBoardActivity.onActivityResult:  图片的高小于画板的一半 --->>> ");
                    newH = canvasHeight / 2;
                }
                //计算缩放比例
                Matrix matrix = new Matrix();
                float scale = (float) newW / bitmapWidth;
                float scale2 = (float) newH / bitmapHeight;
                //取较小的
                scale = scale < scale2 ? scale : scale2;
                matrix.setScale(scale, scale);
                //计算想要缩放的matrix参数
                Log.e("MyLog", "DrawBoardActivity.onActivityResult:  scale --->>> " + scale + "   " + scale2);
//                matrix.postScale(scale, scale);
                Log.e("MyLog", "DrawBoardActivity.onActivityResult:  bitmapWidth --->>> " + bitmapWidth + "   bitmapHeight --->>" + bitmapHeight + "" +
                        "  newW -->>" + newW + "  newH -->>" + newH);
                Bitmap bitmap1 = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, true);
                Log.e("MyLog", "DrawBoardActivity.onActivityResult:   <<<---------分割线--------->>> ");
                /** ************ ******    ****** ************ **/
//                BitmapFactory.Options opt = new BitmapFactory.Options();
//                opt.inPreferredConfig = Bitmap.Config.RGB_565;
//                opt.inPurgeable = true;
//                opt.inInputShareable = true;
//                InputStream inputStream = null;
//                try {
//                    inputStream = new FileInputStream(choosePic);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//                Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, opt);
//                *//** ************ ******    ****** ************ **//*
//                int bitmapWidth = bitmap.getWidth();
//                int bitmapHeight = bitmap.getHeight();
//                int canvasWidth = bitmapWidth;
//                int canvasHeight = bitmapHeight;
//                if (mPaletteView.mBufferCanvas != null) {
//                    canvasWidth = mPaletteView.mBufferCanvas.getWidth();
//                    Log.e("MyLog", "DrawBoardActivity.onActivityResult:  cabvas --->>> " + canvasWidth);
//                    canvasHeight = mPaletteView.mBufferCanvas.getHeight();
//                }
//                int newW = bitmapWidth;
//                int newH = bitmapHeight;
//                if (bitmapWidth >= canvasWidth) {
//                    newW = canvasWidth;
//                }
//                if (bitmapHeight >= canvasHeight) {
//                    newH = canvasHeight;
//                }
//                //计算缩放比例
//                Matrix matrix = new Matrix();
//                float scale = (float) newW / bitmapWidth;
//                float scale2 = (float) newH / bitmapHeight;
//                //计算想要缩放的matrix参数
//                scale = scale < scale2 ? scale : scale2;
//                matrix.postScale(scale, scale);
//                Bitmap bitmap1 = Bitmap.createBitmap(bitmap, 0, 0, newW, newH, matrix, true);
//                Log.e("MyLog", "DrawBoardActivity.onActivityResult: bitmap1  --->>> " + (bitmap1 != null));

                mPaletteView.drawImage(bitmap1);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}
