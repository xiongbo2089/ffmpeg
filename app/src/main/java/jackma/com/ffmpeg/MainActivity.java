package jackma.com.ffmpeg;

import android.Manifest;
import android.hardware.camera2.CameraCharacteristics;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.File;

import jackma.com.ffmpeg.capture.Camera2Helper;
import jackma.com.ffmpeg.util.PermissionUtils;

public class MainActivity extends AppCompatActivity implements Camera2Helper.AfterDoListener{

    private TextView mSrtartPush;
    private TextView mSwitch;
    private File mFile;
    private Camera2Helper mCamera2Helper;
    private AutoFitTextureView textureView;
    private TextView mTakePic;
    public static final String PHOTO_PATH = Environment.getExternalStorageDirectory().getPath();
    public static final String PHOTO_NAME = "camera2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textureView = findViewById(R.id.surface);
        mTakePic = findViewById(R.id.take_pic);
        mSrtartPush = findViewById(R.id.btn_push);
        mSwitch = findViewById(R.id.btn_camera_switch);
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
                if(mSrtartPush.getText().equals("开始直播")){
                    mCamera2Helper.startCameraPreView(CameraCharacteristics.LENS_FACING_FRONT);
                    mSrtartPush.setText("停止直播");
                }else{
                //    mVideoPusher.surfaceDestroyed(surfaceView.getHolder());
                    mSrtartPush.setText("开始直播");
                }
            }
        });

        mSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera2Helper.switchCamera();
            }
        });
    }

    private void init(){
        mFile = new File(PHOTO_PATH, PHOTO_NAME + ".jpg");
        mTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCamera2Helper.takePicture();
            }
        });
        mCamera2Helper = Camera2Helper.getInstance(MainActivity.this,textureView,mFile);
        mCamera2Helper.setAfterDoListener(this);
    }

}
