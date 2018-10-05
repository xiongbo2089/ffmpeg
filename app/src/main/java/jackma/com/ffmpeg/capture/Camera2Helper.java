package jackma.com.ffmpeg.capture;

import android.hardware.Camera;
import android.util.Log;

public class Camera2Helper{

	private Camera mCamera;
	private Camera.Parameters parameters;

	void releaseCamera() {
		if (mCamera != null) {
			Log.d("initCamera3","releaseCamera");
			mCamera.setPreviewCallbackWithBuffer(null);
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}
	/**
	 * 开启摄像头
	 */
	private void openCamera(){
		if(null == mCamera){
			try {
				mCamera = Camera.open(LiveConfig.videoCamera);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("打开摄像头失败", e);
			}
		}
		parameters = mCamera.getParameters();
	}
}
