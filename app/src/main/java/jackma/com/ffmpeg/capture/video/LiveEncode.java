package jackma.com.ffmpeg.capture.video;

import android.hardware.Camera;
import android.media.MediaCodec;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import jackma.com.ffmpeg.capture.VideoGet;
import jackma.com.ffmpeg.capture.LiveBuild;
import jackma.com.ffmpeg.capture.audio.LiveAudioEncode;
import jackma.com.ffmpeg.capture.audio.LiveAudioGet;
import jackma.com.ffmpeg.capture.bean.VideoEncodeType;

/**
 */

public class LiveEncode {

    //视频
    private LinkedBlockingQueue<byte[]> videoQueue;
    private VideoGet liveVideo;
    private LiveVideoEncode liveVideoEncode;
    private Thread videoEncodeThread;
    private boolean videoEncodeStart;
    private int colorFormat;
    //音频
    private LiveAudioGet liveAudioGet;
    private LinkedBlockingQueue<byte[]> audioQueue;
    private Thread audioEncodeThread;
    private boolean audioEncodeStart;
    private LiveAudioEncode liveAudioEncode;

    private LiveBuild build;
    private LiveEncodeListener encodeListener;
    private Camera mCamera;

    public void setEncodeListener(LiveEncodeListener encodeListener) {
        this.encodeListener = encodeListener;
    }

    public static LiveEncode newInstance(){
        return new LiveEncode();
    }

    private LiveEncode() {
        colorFormat = -1;
        videoQueue = new LinkedBlockingQueue<>();
        //视频编码类
        liveVideoEncode = LiveVideoEncode.newInstance();
        liveVideoEncode.setVideoListener(new LiveVideoEncode.OnVideoEncodeListener() {
            @Override
            public void videoToYuv420(byte[] yuv,Camera camera) {
                if(null != encodeListener){
                    encodeListener.videoToYuv420sp(yuv,camera);
                }

            }

            @Override
            public void videoToHard(ByteBuffer bb, MediaCodec.BufferInfo vBufferInfo) {
                if(null != encodeListener && null != bb){
                    encodeListener.videoToHard(bb,vBufferInfo);
                }
            }

            @Override
            public void addCallbackBuffer(byte[] old) {
                liveVideo.addCallbackBuffer(old);
            }

            @Override
            public void pic(byte[] data,Camera camera) {
                encodeListener.pic(data,camera);
            }
        });
        //视频采集类
        liveVideo = VideoGet.newInstance();
        liveVideo.setVideoListener(new VideoGet.LiveVideoListener() {
            @Override
            public void onPreviewFrame(byte[] data) {
                try {
                    if (videoEncodeStart) {
                        videoQueue.put(data);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void pic(byte[] data, Camera camera) {
                encodeListener.pic(data,camera);
            }
        });
        //音频采集
        audioQueue = new LinkedBlockingQueue<>();
        liveAudioGet = LiveAudioGet.newInstance();
        liveAudioGet.initAudio();
        liveAudioGet.setListener(new LiveAudioGet.LiveAudioListener() {
            @Override
            public void audioRead(byte[] audio) {
                audioQueue.add(audio);
            }
        });
        //音频编码
        liveAudioEncode = LiveAudioEncode.newInstance();
        liveAudioEncode.setAudioListener(new LiveAudioEncode.LiveAudioEncodeListener() {
            @Override
            public void audioEncode(ByteBuffer bb, MediaCodec.BufferInfo aBufferInfo) {
                if(null != encodeListener) {
                    encodeListener.audioToHard(bb,aBufferInfo);
                }
            }
        });
    }

    public int getSampleRate(){
        return liveAudioGet.sampleRate;
    }

    public int getChannelConfig(){
        return liveAudioGet.channelConfig;
    }

    public List<Camera.Size> getCameraSize(){
        return liveVideo.getCameraSize();
    }

    public void initEncode(LiveBuild builds) {
        this.build = builds;
        liveVideoEncode.initVideoEncode(build);
        try {
            if (build.getVideoEncodeType() == VideoEncodeType.HARD) {
                colorFormat = liveVideoEncode.initVideoEncoder(build.getVideoWidth(), build.getVideoHeight(), build.getFps(),build.getBitrate());
            }
            liveAudioEncode.initAudioEncoder(liveAudioGet.sampleRate,liveAudioGet.channelConfig);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startEncode(){
        startVideoEncode();
        startAudioEncode();
    }

    private void startAudioEncode(){
        liveAudioGet.startAudio();
        audioEncodeThread = new Thread(){
            @Override
            public void run() {
                liveAudioEncode.startEncode();
                while (audioEncodeStart && !Thread.interrupted()){
                    try {
                        Log.d("audio_queue_size",audioQueue.size()+"");
                        byte[] data = audioQueue.take();
                        if(build.getVideoEncodeType() == VideoEncodeType.HARD){
                            liveAudioEncode.encodeAudioData(data);
                        }else {
                            if(null != encodeListener){
                                encodeListener.audioToSoft(data);
                            }
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        audioEncodeStart = true;
        audioEncodeThread.start();
    }

    private void startVideoEncode(){
        mCamera = liveVideo.startCamera(build);
        videoEncodeThread = new Thread() {
            @Override
            public void run() {
                if (build.getVideoEncodeType() == VideoEncodeType.HARD) {
                    liveVideoEncode.startEncoder();
                }
                while (videoEncodeStart && !Thread.interrupted()) {
                    try {
                        Log.d("vedio_queue_size",videoQueue.size()+"");
                        byte[] bytes = videoQueue.take();
                        liveVideoEncode.encodeData(bytes,colorFormat,mCamera);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        videoEncodeStart = true;
        videoEncodeThread.start();
    }

    public void releaseEncode(){
        liveVideo.releaseCamera();
        liveVideoEncode.releaseVideo();
        stopVideoEncode();
        liveAudioGet.stopAudio();
        stopAudioEncode();
    }


    private void stopVideoEncode() {
        videoEncodeStart = false;
        videoEncodeThread.interrupt();
        videoQueue.clear();
    }

    public void stopAudioEncode(){
        audioEncodeStart = false;
        audioEncodeThread.interrupt();
        audioQueue.clear();
    }

}
