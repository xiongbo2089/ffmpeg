package jackma.com.ffmpeg.ffmpeg;

import android.app.Activity;
import android.media.MediaCodec;

import java.nio.ByteBuffer;

import jackma.com.ffmpeg.capture.LiveBuild;
import jackma.com.ffmpeg.capture.LiveConfig;
import jackma.com.ffmpeg.capture.LiveRop;
import jackma.com.ffmpeg.capture.listener.LiveNativeInitListener;
import jackma.com.ffmpeg.capture.video.LiveEncodeListener;


/**
 *
 * @author 邓治民
 * date 2017/9/5 15:46
 */

public class LiveFfmpegManager {

    public static LiveRop build(Activity activity){
        return LiveRop.getInstance().build(activity).setEncodeListener(new LiveEncodeListener() {
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

}
