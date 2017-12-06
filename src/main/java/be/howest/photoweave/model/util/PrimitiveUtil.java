package be.howest.photoweave.model.util;

/**
 * Created by tomdo on 30/11/2017.
 */
public class PrimitiveUtil {
    public PrimitiveUtil() {

    }

    public static byte[] decomposeIntToBytes(int value) {
        byte[] result = new byte[4];

        result[0] = (byte) ((value >> 24) & 0xff);
        result[1] = (byte) ((value >> 16) & 0xff);
        result[2] = (byte) ((value >> 8) & 0xff);
        result[3] = (byte) (value & 0xff);

        return result;
    }

    public static int composeIntFromBytes(byte[] decomposedValue) {
        return (((int) decomposedValue[0]) << 24)
                | (((int) decomposedValue[1]) << 16)
                | (((int) decomposedValue[2]) << 8)
                | ((int) decomposedValue[3]);
    }
}
