package jackma.com.ffmpeg;

import android.Manifest;
import android.media.MediaCodec;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import java.nio.ByteBuffer;

import jackma.com.ffmpeg.capture.CameraHelper;
import jackma.com.ffmpeg.capture.LiveBuild;
import jackma.com.ffmpeg.capture.LiveConfig;
import jackma.com.ffmpeg.capture.LiveRop;
import jackma.com.ffmpeg.capture.bean.VideoEncodeType;
import jackma.com.ffmpeg.capture.listener.LiveNativeInitListener;
import jackma.com.ffmpeg.capture.video.LiveEncodeListener;
import jackma.com.ffmpeg.ffmpeg.LivePusher;
import jackma.com.ffmpeg.util.PermissionUtils;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private LiveRop liveRop;

    private TextView mSrtartPush;
    private TextView mSwitch;
    private String rtmpPath;
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
        PermissionUtils.checkPermission(this, Manifest.permission.RECORD_AUDIO,
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
                        PermissionUtils.requestPermission(MainActivity.this,Manifest.permission.RECORD_AUDIO,1);
                    }
                });
        PermissionUtils.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE,
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
                        PermissionUtils.requestPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE,1);
                    }
                });

        mSrtartPush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rtmpPath = "file:/" + Environment.getExternalStorageDirectory().getPath() + "/1/1.mp4";
                //rtmpPath = "rtmp://192.168.0.4:1935/live";
                liveRop =  LiveRop.getInstance().build(MainActivity.this)
                        .setHolder(surfaceView.getHolder())
                        .setRtmpUrl(rtmpPath)
                        .setVideoWidth(surfaceView.getWidth())
                        .setVideoEncodeType(mType)
                        .setVideoHeight(surfaceView.getHeight())
                        .setBitrate(bitrate*1000);
                setEncodeListener();

                liveRop.initEncode();
                liveRop.startEncode();
            }
        });

        mSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    private void setEncodeListener(){
        liveRop.setEncodeListener(new LiveEncodeListener() {
            @Override
            public void videoToYuv420sp(byte[] yuv) {
                LivePusher.getInscance().addVideo(yuv, LiveConfig.isBack);
            }

            @Override
            public void videoToHard(ByteBuffer bb, MediaCodec.BufferInfo info) {

            }

            @Override
            public void audioToHard(ByteBuffer bb, MediaCodec.BufferInfo info) {

            }

            @Override
            public void audioToSoft(byte[] data) {

            }
        }).setNativeInitListener(new LiveNativeInitListener() {
            @Override
            public void initNative(LiveBuild build) {
                LivePusher.getInscance().init(build.getVideoWidth(),build.getVideoHeight(),build.getRtmpUrl());
            }

            @Override
            public void releaseNative() {

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
