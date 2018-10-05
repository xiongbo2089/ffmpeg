package jackma.com.ffmpeg.capture.listener;

import jackma.com.ffmpeg.capture.LiveBuild;

/**
 * Created by 2018/9/2.
 */

public interface LiveNativeInitListener {

    void initNative(LiveBuild build);

    void releaseNative();

}
