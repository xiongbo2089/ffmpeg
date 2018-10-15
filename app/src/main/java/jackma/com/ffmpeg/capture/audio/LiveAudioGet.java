package jackma.com.ffmpeg.capture.audio;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

/**
 *
 * 音频数据采集
 */
public class LiveAudioGet {
    private static final String TAG = LiveAudioGet.class.getName();
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private int audioChannel = AudioFormat.CHANNEL_CONFIGURATION_STEREO;
    private AudioRecord mAudioRecord;
    private byte[] buffer;
    private Thread audioThread;
    private boolean isStart;
    private LiveAudioListener listener;
    public int sampleRate;
    public int channelConfig;

    public void setListener(LiveAudioListener listener) {
        this.listener = listener;
    }

    public static LiveAudioGet newInstance(){
        return new LiveAudioGet();
    }

    private LiveAudioGet(){}

    public void initAudio(){
        int[] sampleRates = {44100, 22050, 16000, 11025};
        for (int sampleRate :sampleRates) {
            //编码制式
            // stereo 立体声，
            int buffsize = 2 * AudioRecord.getMinBufferSize(sampleRate, audioChannel, audioFormat);
            mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, audioChannel,
                    audioFormat, buffsize);
            if (mAudioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
                continue;
            }
            this.buffer = new byte[Math.min(4096, buffsize)];
            this.sampleRate = sampleRate;
            channelConfig = audioChannel == AudioFormat.CHANNEL_CONFIGURATION_STEREO ? 2 : 1;
            break;
        }
    }

    public void startAudio(){
        audioThread = new Thread(){
            @Override
            public void run() {
                mAudioRecord.startRecording();
                while (isStart && !Thread.interrupted()){
                    int size = mAudioRecord.read(buffer, 0, buffer.length);
                    if(size<0){
                        Log.i(TAG, "audio no data to read");
                        continue;
                    }
                    if (isStart) {
                        byte[] audio = new byte[size];
                        System.arraycopy(buffer, 0, audio, 0, size);
                        if(null != listener)
                            listener.audioRead(audio);
                    }else
                        break;
                }
            }
        };
        isStart = true;
        audioThread.start();
    }

    public void stopAudio(){
        isStart = false;
        if(null != audioThread)
            audioThread.interrupt();
        mAudioRecord.stop();
//        mAudioRecord.release();
    }

    public interface LiveAudioListener{
        void audioRead(byte[] audio);
    }

}
