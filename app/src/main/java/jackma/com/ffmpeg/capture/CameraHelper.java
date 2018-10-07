package jackma.com.ffmpeg.capture;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.util.List;

import static android.hardware.Camera.Parameters.FOCUS_MODE_AUTO;

public class CameraHelper {

	private LiveBuild build;
	private Camera mCamera;
	private Camera.Parameters parameters;
	private LiveVideoListener videoListener;

	public void setVideoListener(LiveVideoListener videoListener) {
		this.videoListener = videoListener;
	}

	public static CameraHelper newInstance(){
		return new CameraHelper();
	}

	private CameraHelper(){}

	/**
	 * 初始化视频采集
	 */
	public void startCamera(LiveBuild builds){
		this.build = builds;
		releaseCamera();
		openCamera();
		setCameraParameters();
		setCameraDisplayOrientation(build.getActivity(),LiveConfig.videoCamera,mCamera);
		try {
			if(null != build.getHolder()){
				mCamera.setPreviewDisplay(build.getHolder());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		mCamera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
			@Override
			public void onPreviewFrame(byte[] bytes, Camera camera) {
				if(null != bytes){
					if(null != videoListener){
						videoListener.onPreviewFrame(bytes);
					}else{
						addCallbackBuffer(bytes);
					}
				}else {
					addCallbackBuffer(new byte[calculateFraeSize(ImageFormat.NV21)]);
				}
			}
		});
		addCallbackBuffer(new byte[calculateFraeSize(ImageFormat.NV21)]);
		mCamera.startPreview();
	}

	public void releaseCamera() {
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

	private void setCameraParameters(){

		parameters.setPreviewSize(build.getVideoWidth(),build.getVideoHeight());
		List<String> supportedFocusModes = parameters.getSupportedFocusModes();
		for (int i = 0; null != supportedFocusModes && i < supportedFocusModes.size(); i++) {
			if (FOCUS_MODE_AUTO.equals(supportedFocusModes.get(i))) {
				parameters.setFocusMode(FOCUS_MODE_AUTO);
				break;
			}
		}
		parameters.setPreviewFormat(ImageFormat.NV21);
		mCamera.setParameters(parameters);
	}

	public void addCallbackBuffer(byte[] bytes) {
		if(null != mCamera)
			mCamera.addCallbackBuffer(bytes);
	}

	public List<Camera.Size> getCameraSize(){
		releaseCamera();
		openCamera();
		return parameters.getSupportedPreviewSizes();
	}

	private static void setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
		Camera.CameraInfo info =
				new Camera.CameraInfo();
		Camera.getCameraInfo(cameraId, info);
		int rotation = activity.getWindowManager().getDefaultDisplay()
				.getRotation();
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
			result = (360 - result) % 360;  // compensate the mirror
		} else {  // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		camera.setDisplayOrientation(result);
	}

	/**
	 * 获取数据总长度
	 */
	private int calculateFraeSize(int format) {
		return  build.getVideoWidth() * build.getVideoHeight() * ImageFormat.getBitsPerPixel(format) / 8;
	}

	public interface LiveVideoListener{
		void onPreviewFrame(byte[] data);
	}
}
