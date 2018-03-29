package com.bh.ocr.activuty;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bh.ocr.R;
import com.bh.ocr.event.DataEvent;
import com.bh.ocr.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

/**
 * 照相机界面
 * 拍照并且回传给MainActivity
 * */
public class CameraActivity extends AppCompatActivity{

    private Camera camera;
    private boolean isPreview = false;
    private ImageView ex;
    private SurfaceView mSurfaceView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mSurfaceView = findViewById(R.id.sv);
        // 获得 SurfaceHolder 对象
        SurfaceHolder mSurfaceHolder = mSurfaceView.getHolder();

        // 设置 Surface 格式
        // 参数： PixelFormat中定义的 int 值 ,详细参见 PixelFormat.java
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);

        // 如果需要，保持屏幕常亮
        mSurfaceHolder.setKeepScreenOn(true);

        // 添加 Surface 的 callback 接口
        mSurfaceHolder.addCallback(mSurfaceCallback);

        ex = findViewById(R.id.ex_box);

        Intent it = getIntent();
        int type = it.getIntExtra("type", -1);
        if(type == 0){
            ex.setImageResource(R.mipmap.k);
        }else if(type == 1){
            ex.setImageResource(R.mipmap.k2);
        }

        Button btn = findViewById(R.id.btn_camera);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.takePicture(null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        Bitmap mbitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        Bitmap sizeBitmap = Bitmap.createScaledBitmap(mbitmap, 1920, 1080, true);
                        Bitmap rectBitmap = Bitmap.createBitmap(sizeBitmap, 340, 130, 1250, 840);//截取
//                        Log.i("wsy",bitmapzip.getWidth()+"    "+bitmapzip.getHeight());
//                        Log.i("wsy",bitmap.getWidth()+"    "+bitmap.getHeight());
                        DataEvent d = new DataEvent();
                        d.setType(CameraActivity.this.getIntent().getIntExtra("type",-1));
                        d.setImg(Utils.bitmapToBase64(rectBitmap));
                        EventBus.getDefault().post(d);
                        finish();
                    }
                });
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();//如果点home键，销毁当前页，返回前一页。
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(camera != null) {
            camera.release();
        }
        finish();
    }

    private SurfaceHolder.Callback mSurfaceCallback = new SurfaceHolder.Callback() {

        /**
         *  在 Surface 首次创建时被立即调用：获得焦点时。一般在这里开启画图的线程
         * @param surfaceHolder 持有当前 Surface 的 SurfaceHolder 对象
         */
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            try {
                // Camera,open() 默认返回的后置摄像头信息
                camera = Camera.open();//打开硬件摄像头，这里导包得时候一定要注意是android.hardware.Camera

                //此处也可以设置摄像头参数

//                WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);//得到窗口管理器
//                Display display  = wm.getDefaultDisplay();//得到当前屏幕
                Camera.Parameters parameters = camera.getParameters();//得到摄像头的参数
                parameters.setJpegQuality(100);//设置照片的质量
                //这里不能有 窗口的宽高 要不然 8.0 预览会白屏

//                Camera.Size closelyPreSize = Utils.getCloselyPreSize(display.getWidth(), display.getHeight(), parameters.getSupportedPreviewSizes());
                parameters.setPreviewSize(1920,1080);//设置预览尺寸
//                parameters.setPreviewSize(parameters.getPictureSize().width,parameters.getPictureSize().height);
                parameters.setPictureSize(1920,1080);//设置照片尺寸
                //parameters.setPictureSize(display.getWidth(), display.getHeight());//设置照片的大小

                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 连续对焦模式
                camera.setParameters(parameters);

                //设置角度，此处 CameraId 我默认 为 0 （后置）
                // CameraId 也可以 通过 参考 Camera.open() 源码 方法获取
                setCameraDisplayOrientation(CameraActivity.this,0,camera);
                camera.setPreviewDisplay(surfaceHolder);//通过SurfaceView显示取景画面
                camera.startPreview();//开始预览
                isPreview = true;//设置是否预览参数为真
            } catch (IOException e) {
                Log.e("surfaceCreated", e.toString());
            }
        }


        /**
         *  在 Surface 格式 和 大小发生变化时会立即调用，可以在这个方法中更新 Surface
         * @param surfaceHolder   持有当前 Surface 的 SurfaceHolder 对象
         * @param format          surface 的新格式
         * @param width           surface 的新宽度
         * @param height          surface 的新高度
         */
        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {

        }

        /**
         *  在 Surface 被销毁时立即调用：失去焦点时。一般在这里将画图的线程停止销毁
         * @param surfaceHolder 持有当前 Surface 的 SurfaceHolder 对象
         */
        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            if(camera != null){
                if(isPreview){//正在预览
                    camera.stopPreview();
                    camera.release();
                }
            }
        }
    };

    /**
     * 设置 摄像头的角度
     *
     * @param activity 上下文
     * @param cameraId 摄像头ID（假如手机有N个摄像头，cameraId 的值 就是 0 ~ N-1）
     * @param camera   摄像头对象
     */
    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, Camera camera) {

        Camera.CameraInfo info = new Camera.CameraInfo();
        //获取摄像头信息
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        //获取摄像头当前的角度
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            // 前置摄像头
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else {
            // 后置摄像头
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

}
