package jackma.com.ffmpeg.capture.listener;

import android.view.SurfaceHolder;

import jackma.com.ffmpeg.capture.bean.VideoEncodeType;
import jackma.com.ffmpeg.capture.video.LiveEncodeListener;

/**
 *
 * date 2018/10/2
 */

public interface LiveRopMain {

    LiveRopMain setEncodeListener(LiveEncodeListener encodeListener);

    LiveRopMain setBitrate(int bitrate);

    LiveRopMain setFps(int fps);

    LiveRopMain setVideoEncodeType(VideoEncodeType videoEncodeType);

    LiveRopMain setRtmpUrl(String rtmpUrl);

    LiveRopMain setVideoWidth(int videoWidth);

    LiveRopMain setVideoHeight(int videoHeight);

    LiveRopMain setHolder(SurfaceHolder holder);

    void initEncode();

    void startEncode();

    void releaseEncode();
}
