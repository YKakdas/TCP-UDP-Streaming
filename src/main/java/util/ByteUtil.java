package util;

public class ByteUtil {
    public static byte[] getSubArray(int start, int length, byte[] src) {
        if (length > src.length) {
            length = src.length - start;
        } else {
            length -= start;
        }
        byte[] dest = new byte[length];
        System.arraycopy(src, start, dest, 0, length);
        return dest;
    }

    public static void mergeArrays(byte[] input, byte[] result, int start) {
        for (int i = 0; i < input.length; i++) {
            result[start + i] = input[i];
        }
    }

    public static byte[] splitArray(byte[] src, int length) {
        byte[] split = new byte[length];
        for (int i = 0; i < length; i++) {
            split[i] = src[i];
        }
        return split;
    }
}
