package jackma.com.ffmpeg.ffmpeg;

public class LivePusher {

    static {
        System.loadLibrary("native-lib");
    }

    private static LivePusher livePush;

    public static LivePusher getInscance(){
        if(null == livePush){
            synchronized (LivePusher.class){
                if(null == livePush){
                    livePush = new LivePusher();
                }
            }
        }
        return livePush;
    }

    public native void init(int width,int height,String rtmpPath);

    public native void addVideo(byte[] data,boolean isBack);

}
