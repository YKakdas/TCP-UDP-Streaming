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
        System.arraycopy(input, 0, result, start, input.length);
    }

    public static byte[] splitArray(byte[] src, int length) {
        byte[] split = new byte[length];
        System.arraycopy(src, 0, split, 0, length);
        return split;
    }
}
