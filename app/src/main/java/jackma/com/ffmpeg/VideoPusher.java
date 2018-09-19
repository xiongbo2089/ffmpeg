package jackma.com.ffmpeg;

import android.hardware.Camera;
import android.hardware.Camera2;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PreviewCallback;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;



import java.io.IOException;

import jackma.com.ffmpeg.bean.VideoParam;

public class VideoPusher implements Callback, PreviewCallback {

	private SurfaceHolder surfaceHolder;
	private Camera mCamera;
	private VideoParam videoParams;
	private byte[] buffers;
	private boolean isPushing = false;

	public VideoPusher(SurfaceHolder surfaceHolder, VideoParam videoParams) {
		this.surfaceHolder = surfaceHolder;
		this.videoParams = videoParams;
		surfaceHolder.addCallback(this);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		startPreview();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		
	}
	
	/**
	 * 切换摄像头
	 */
	public void switchCamera() {
		if(videoParams.getCameraId() == CameraInfo.CAMERA_FACING_BACK){
			videoParams.setCameraId(CameraInfo.CAMERA_FACING_FRONT);
		}else{
			videoParams.setCameraId(CameraInfo.CAMERA_FACING_BACK);
		}
		//重新预览
		stopPreview();
		startPreview();
	}
	
	/**
	 * 开始预览
	 */
	private void startPreview() {
		try {
			//SurfaceView初始化完成，开始相机预览
			mCamera = Camera.open(videoParams.getCameraId());
			mCamera.setPreviewDisplay(surfaceHolder);
			//获取预览图像数据
			buffers = new byte[videoParams.getWidth() * videoParams.getHeight() * 4];
			mCamera.addCallbackBuffer(buffers);
			mCamera.setPreviewCallbackWithBuffer(this);
			
			mCamera.startPreview();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 停止预览
	 */
	private void stopPreview() {
		if(mCamera != null){			
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		if(mCamera != null){
			mCamera.addCallbackBuffer(buffers);
		}
	}

}
