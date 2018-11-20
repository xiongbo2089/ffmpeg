package jackma.com.ffmpeg;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.media.MediaCodec;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;

import jackma.com.ffmpeg.capture.VideoGet;
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
    private VideoGet mVideoGet;
    private SurfaceView surfaceView;
    private TextView mTakePic;
    public static final String PHOTO_PATH = Environment.getExternalStorageDirectory().getPath();
    public static final String PHOTO_NAME = "camera2";

    private static final String SD_PATH = "/sdcard/1/pic/";
    private static final String IN_PATH = "/1/pic/";
    public  static int abcaaa = 0;
    public  static  boolean canpic = false;

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

        mTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canpic = true;
            }
        });
    }

        public void runInPreviewFrame(byte[] data, Camera camera) {

            ByteArrayOutputStream baos;
            byte[] rawImage;
            Bitmap bitmap;
            camera.setOneShotPreviewCallback(null);
            //处理data
            Camera.Size previewSize = camera.getParameters().getPreviewSize();//获取尺寸,格式转换的时候要用到
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            newOpts.inJustDecodeBounds = true;
            YuvImage yuvimage = new YuvImage(
                    data,
                    ImageFormat.NV21,
                    previewSize.width,
                    previewSize.height,
                    null);
            baos = new ByteArrayOutputStream();
            yuvimage.compressToJpeg(new Rect(0, 0, previewSize.width, previewSize.height), 100, baos);// 80--JPG图片的质量[0-100],100最高
            rawImage = baos.toByteArray();
            //将rawImage转换成bitmap
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            bitmap = BitmapFactory.decodeByteArray(rawImage, 0, rawImage.length, options);

            saveBitmap(this,bitmap);
        }

    /**
     * 随机生产文件名
     *
     * @return
     */
    private static String generateFileName() {
        return UUID.randomUUID().toString();
    }
    /**
     * 保存bitmap到本地
     *
     * @param context
     * @param mBitmap
     * @return
     */
    public static String saveBitmap(Context context, Bitmap mBitmap) {
        String savePath;
        File filePic;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            savePath = SD_PATH;
        } else {
            savePath = context.getApplicationContext().getFilesDir()
                    .getAbsolutePath()
                    + IN_PATH;
        }
        try {
            abcaaa ++;
            filePic = new File(savePath + generateFileName() + "" + abcaaa  + ".jpg");
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

        return filePic.getAbsolutePath();
    }

    private void setEncodeListener(){
        liveRop.setEncodeListener(new LiveEncodeListener() {
            @Override
            public void videoToYuv420sp(byte[] yuv, Camera camera) {
                LivePusher.getInscance().addVideo(yuv, LiveConfig.isBack);
                if (canpic) {
                    runInPreviewFrame(yuv, camera);
                }
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

            @Override
            public void pic(byte[] data,Camera camera) {
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
