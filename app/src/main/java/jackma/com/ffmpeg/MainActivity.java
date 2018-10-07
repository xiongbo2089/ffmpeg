package jackma.com.ffmpeg;

import android.Manifest;
import android.hardware.camera2.CameraCharacteristics;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import java.io.File;

import jackma.com.ffmpeg.capture.CameraHelper;
import jackma.com.ffmpeg.capture.LiveBuild;
import jackma.com.ffmpeg.capture.LiveRop;
import jackma.com.ffmpeg.capture.bean.VideoEncodeType;
import jackma.com.ffmpeg.ffmpeg.LiveFfmpegManager;
import jackma.com.ffmpeg.util.PermissionUtils;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private LiveRop liveRop;

    private TextView mSrtartPush;
    private TextView mSwitch;
    private String rtmpPath = "rtmp://ip:192.168.0.2:9009/live";
    private int bitrate = 800;
    private VideoEncodeType mType;
    private CameraHelper mCameraHelper;
    private SurfaceView surfaceView;
    private TextView mTakePic;
    public static final String PHOTO_PATH = Environment.getExternalStorageDirectory().getPath();
    public static final String PHOTO_NAME = "camera2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        surfaceView = findViewById(R.id.surface);
        mTakePic = findViewById(R.id.take_pic);
        mSrtartPush = findViewById(R.id.btn_push);
        mSwitch = findViewById(R.id.btn_camera_switch);

        surfaceView.setKeepScreenOn(true);
        SurfaceHolder mSurfaceHolder = surfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        //相机图像的预览

        init();

        PermissionUtils.checkPermission(this, Manifest.permission.CAMERA,
                new PermissionUtils.PermissionCheckCallBack() {
                    @Override
                    public void onHasPermission() {
                        // 已授予权限
                    }

                    @Override
                    public void onUserHasAlreadyTurnedDown(String... permission) {
                        // 上一次申请权限被拒绝，可用于向用户说明权限原因，然后调用权限申请方法。
                    }

                    @Override
                    public void onUserHasAlreadyTurnedDownAndDontAsk(String... permission) {
                        // 第一次申请权限或被禁止申请权限，建议直接调用申请权限方法。
                        PermissionUtils.requestPermission(MainActivity.this,Manifest.permission.CAMERA,1);
                    }
                });

        mSrtartPush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                liveRop =  LiveFfmpegManager.build(MainActivity.this)
                        .setHolder(surfaceView.getHolder())
                        .setRtmpUrl(rtmpPath)
                        .setVideoWidth(surfaceView.getWidth())
                        .setVideoEncodeType(mType)
                        .setVideoHeight(surfaceView.getHeight())
                        .setBitrate(bitrate*1000);
                liveRop.initEncode();
            }
        });

        mSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    private void init(){
        mType = VideoEncodeType.SOFT;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (liveRop != null) {
            liveRop.startEncode();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        liveRop.releaseEncode();
    }

}
