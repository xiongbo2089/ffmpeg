package jackma.com.ffmpeg.capture.video;

import android.hardware.Camera;
import android.media.MediaCodec;

import java.nio.ByteBuffer;

/**
 * Created by 2019/10/2.
 */

public interface LiveEncodeListener {

    void videoToYuv420sp(byte[] yuv,Camera camera);

    void videoToHard(ByteBuffer bb, MediaCodec.BufferInfo info);

    void audioToHard(ByteBuffer bb, MediaCodec.BufferInfo info);

    void audioToSoft(byte[] data);

    void pic(byte[] data,Camera camera);

}
