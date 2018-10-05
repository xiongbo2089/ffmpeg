package jackma.com.ffmpeg.capture;

import android.app.Activity;
import android.hardware.Camera;

import java.util.List;

/**
 * LiveConfig
 */

public class LiveConfig {

    /**摄像头位置设置*/
    static int videoCamera = Camera.CameraInfo.CAMERA_FACING_FRONT;

    /**是否为后置摄像头，后置数据旋转要顺时针，前置为逆时针旋转*/
    public static boolean isBack = videoCamera == Camera.CameraInfo.CAMERA_FACING_BACK;

    public static List<Camera.Size> getCameraSize(Activity activity){
        return LiveRop.getInstance().build(activity).getCameraSize();
    }

}
