package jackma.com.ffmpeg.capture.audio;

public class AudioSpecificConfig {
	private final static int BYTE_NUMBIT = 8;

	private static void putByte(AudioTag audioTag, long data, int numBit) {
		long bits;
		int num = 0;
		int maxNum = BYTE_NUMBIT - audioTag.getCurrentBit() % BYTE_NUMBIT;
		int curNum = 0;
		while (num < numBit) {
			curNum = Math.min(numBit - num, maxNum);
			bits = data >> (numBit - num - curNum);
			WriteByte(audioTag, bits, curNum);
			num += curNum;
			maxNum = BYTE_NUMBIT;
		}
	}

	private static void WriteByte(AudioTag audioTag, long data, int numBit) {
		long numUsed, idx;
		idx = (audioTag.getCurrentBit() / BYTE_NUMBIT) % 2;
		numUsed = audioTag.getCurrentBit() % BYTE_NUMBIT;
		audioTag.getData()[(int) idx] |= (data & ((1 << numBit) - 1)) << (BYTE_NUMBIT
				- numUsed - numBit);
		audioTag.addCurrentBit(numBit);
	}

	private static int GetSRIndex(int sampleRate) {

		if (92017 <= sampleRate)
			return 0;
		if (75132 <= sampleRate)
			return 1;
		if (55426 <= sampleRate)
			return 2;
		if (46009 <= sampleRate)
			return 3;
		if (37566 <= sampleRate)
			return 4;
		if (27713 <= sampleRate)
			return 5;
		if (23004 <= sampleRate)
			return 6;
		if (18783 <= sampleRate)
			return 7;
		if (13856 <= sampleRate)
			return 8;
		if (11502 <= sampleRate)
			return 9;
		if (9391 <= sampleRate)
			return 10;

		return 11;
	}

	public static byte[] getSpecificInfo() {
		// MAIN:1 LOW(LC):2 SSR:3 LTP:4
		int profile = 2;
		// 采样率
		int sampleRate = 48000;
		// 声道
		int channel = 2;
		AudioTag audioTag = new AudioTag();
		putByte(audioTag, profile, 5);
		putByte(audioTag, GetSRIndex(sampleRate), 4);
		putByte(audioTag, channel, 4);
		return audioTag.getData();
	}

	private static class AudioTag {

		private byte[] data;

		private int currentBit = 0;

		public AudioTag() {
			data = new byte[2];
		}

		public int getCurrentBit() {
			return currentBit;
		}

		public byte[] getData() {
			return data;
		}

		public void addCurrentBit(int currentBit) {
			this.currentBit += currentBit;
		}
	}

}
