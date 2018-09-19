package jackma.com.ffmpeg;

import android.Manifest;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import jackma.com.ffmpeg.bean.VideoParam;
import jackma.com.ffmpeg.util.DisplayUtil;
import jackma.com.ffmpeg.util.PermissionUtils;

public class MainActivity extends AppCompatActivity {
    private TextView mSrtartPush;
    private Button mSwitch;
    private SurfaceView surfaceView;
    private VideoPusher mVideoPusher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        surfaceView = (SurfaceView) findViewById(R.id.surface);

        mSrtartPush = findViewById(R.id.btn_push);
        mSwitch = findViewById(R.id.btn_camera_switch);
        VideoParam videoParam = new VideoParam(DisplayUtil.getWindowWidth(this), DisplayUtil.getWindowHeight(this), Camera.CameraInfo.CAMERA_FACING_BACK);
        //相机图像的预览
        mVideoPusher = new VideoPusher(surfaceView.getHolder(),videoParam);

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
                    mVideoPusher.surfaceCreated(surfaceView.getHolder());
                    mSrtartPush.setText("停止直播");
                }else{
                    mVideoPusher.surfaceDestroyed(surfaceView.getHolder());
                    mSrtartPush.setText("开始直播");
                }
            }
        });

        mSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVideoPusher.switchCamera();
            }
        });
    }
}
