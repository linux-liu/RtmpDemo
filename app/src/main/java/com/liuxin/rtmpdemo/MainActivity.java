package com.liuxin.rtmpdemo;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.EventLog;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.yunovo.rtmppush.RtmpPush;
import com.yunovo.rtmppush.filepush.FilePush;
import com.yunovo.rtmppush.livepush.LivePush;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    String url = "rtmp://10.0.0.178/live/livestream";
    private SurfaceView sv;
    private SurfaceHolder mHolder;
    private int screenWidth = 1280;
    private int screenHeight = 720;

    private Camera mCamera;
    boolean isPreview = false; // 是否在浏览中
    private LivePush livePush = new LivePush();
    private FilePush filePush = new FilePush();
    private boolean isStart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        sv = findViewById(R.id.sv);
        mHolder = sv.getHolder();
        mHolder.addCallback(this);
        checkPermission();

    }

    public boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CAMERA
            }, 1);

        }
        return false;
    }

    public void btnStart(View view) {
        if (mCamera == null) {
            mCamera = getCamera();
        }
        setStartPreview(mCamera, mHolder);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mHolder = holder;
        if (mCamera == null) {
            mCamera = getCamera();

        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();
    }

    private Camera getCamera() {
        Camera camera;
        try {
            //打开相机，默认为后置，可以根据摄像头ID来指定打开前置还是后置
            camera = Camera.open(1);
            if (camera != null && !isPreview) {
                try {
                    Camera.Parameters parameters = camera.getParameters();
                    //对拍照参数进行设置
                    for (Camera.Size size : parameters.getSupportedPictureSizes()) {
                        Log.d("rtmp", size.width + "  " + size.height);
                    }
                    Log.d("rtmp", "============");
                    for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
                        Log.d("rtmp", size.width + "  " + size.height);
                    }
                    parameters.setPreviewSize(screenWidth, screenHeight); // 设置预览照片的大小
                    parameters.setPreviewFpsRange(15000, 15000);
                    parameters.setPictureFormat(ImageFormat.NV21); // 设置图片格式
                    parameters.setPictureSize(screenWidth, screenHeight); // 设置照片的大小
                    camera.setParameters(parameters);

                    setCameraDisplayOrientation(this, 1, camera);
                    camera.setPreviewCallback(new Camera.PreviewCallback() {
                        @Override
                        public void onPreviewFrame(byte[] data, Camera camera) {
                            if (livePush.isLiving()) {
                                livePush.putYUV(data);
                            }
                        }
                    });
                    //指定使用哪个SurfaceView来显示预览图片
                    // 通过SurfaceView显示取景画面


                    //Camera.takePicture()方法进行拍照
                    camera.autoFocus(null); // 自动对焦
                } catch (Exception e) {

                    e.printStackTrace();
                }
                isPreview = true;
            }
        } catch (Exception e) {
            camera = null;
            e.printStackTrace();
            Toast.makeText(this, "无法获取前置摄像头", Toast.LENGTH_LONG);
        }
        return camera;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setCameraDisplayOrientation(this, 1, mCamera);
    }

    public static void setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {
        if (camera == null) return;
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
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
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;   // compensate the mirror
        } else {
            // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    /*
 释放相机资源
  */
    private void releaseCamera() {
        if (livePush != null) {
            livePush.release();
        }
        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
            isPreview = false;
        }
    }


    private void setStartPreview(Camera camera, SurfaceHolder holder) {
        try {
            if (camera == null || holder == null) return;
            camera.setPreviewDisplay(holder);
            //followScreenOrientation(this, camera);
            camera.startPreview();
            livePush.startLive(url);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void btnStop(View view) {
        releaseCamera();

    }

    public void fileStart(View view) {
        filePush.startPush("rtmp://10.0.0.178/live/cctvstream", Environment.getExternalStorageDirectory().getAbsolutePath() + "/test.flv");


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (filePush != null)
            filePush.close();
    }

    public void fileStop(View view) {
        if (filePush != null)
            filePush.close();
    }
}
