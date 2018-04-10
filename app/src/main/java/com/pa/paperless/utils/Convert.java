package com.pa.paperless.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Created by Administrator on 2018/4/2.
 */

public class Convert {
    public static byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        byte[] buffer=new byte[1024*4];
        int n=0;
        while ( (n=in.read(buffer)) !=-1) {
            out.write(buffer,0,n);
        }
        return out.toByteArray();
    }

    /**
     * 十六进制转换成int
     *
     * @param c
     * @return
     */
    public static int hexCharToInt(char c) {
        if ((c >= '0') && (c <= '9'))
            return (c - '0');
        if ((c >= 'A') && (c <= 'F'))
            return (c - 'A' + 10);
        if ((c >= 'a') && (c <= 'f'))
            return (c - 'a' + 10);

        throw new RuntimeException("invalid hex char '" + c + "'");
    }

    /**
     * byte数组中取int数值，本方法适用于(低位在前，高位在后)的顺序。
     *
     * @param ary
     *            byte数组
     * @param offset
     *            从数组的第offset位开始
     * @return int数值
     */
    public static int bytesToInt(byte[] ary, int offset) {
        int value;
        value = (int) ((ary[offset]&0xFF)
                | ((ary[offset+1]<<8) & 0xFF00)
                | ((ary[offset+2]<<16)& 0xFF0000)
                | ((ary[offset+3]<<24) & 0xFF000000));
        return value;
    }

    /**
     * byte数组转换成ASCII码
     *
     * @param bytes
     * @param offset
     * @param dateLen
     * @return
     */
    public static String bytesToAscii(byte[] bytes, int offset, int dateLen) {
        if ((bytes == null) || (bytes.length == 0) || (offset < 0) || (dateLen <= 0)) {
            return null;
        }
        if ((offset >= bytes.length) || (bytes.length - offset < dateLen)) {
            return null;
        }

        String asciiStr = null;
        byte[] data = new byte[dateLen];
        System.arraycopy(bytes, offset, data, 0, dateLen);
        try {
            asciiStr = new String(data, "ISO8859-1");
        } catch (UnsupportedEncodingException e) {
        }
        return asciiStr;
    }

    /**
     * byte数组转换成ASCII码
     *
     * @param bytes
     * @param dateLen
     * @return
     */
    public static String bytesToAscii(byte[] bytes, int dateLen) {
        return bytesToAscii(bytes, 0, dateLen);
    }

    public static String bytesToAscii(byte[] bytes) {
        return bytesToAscii(bytes, 0, bytes.length);
    }

    /**
     * byte数组转成16进制字符串
     *
     * @param bytes
     * @param offset
     * @param len
     * @return
     */
    public static String bytesToHexString(byte[] bytes, int offset, int len) {
        if (bytes == null)
            return "null!";

        StringBuilder ret = new StringBuilder(2 * len);

        for (int i = 0; i < len; ++i) {
            int b = 0xF & bytes[(offset + i)] >> 4;
            ret.append("0123456789abcdef".charAt(b));
            b = 0xF & bytes[(offset + i)];
            ret.append("0123456789abcdef".charAt(b));
        }

        return ret.toString();
    }

    public static String bytesToHexString(byte[] bytes, int len) {
        return ((bytes == null) ? "null!" : bytesToHexString(bytes, 0, len));
    }

    public static String bytesToHexString(byte[] bytes) {
        return ((bytes == null) ? "null!" : bytesToHexString(bytes, bytes.length));
    }

    /**
     * 16进制字符串转成byte数组
     *
     * @param s
     * @return
     */
    public static byte[] hexStringToBytes(String s) {
        if (s == null)
            return null;

        int sz = s.length();
        try {
            byte[] ret = new byte[sz / 2];
            for (int i = 0; i < sz; i += 2) {
                ret[(i / 2)] = (byte) (hexCharToInt(s.charAt(i)) << 4 | hexCharToInt(s.charAt(i + 1)));
            }

            return ret;
        } catch (RuntimeException re) {
        }
        return null;
    }

    public static byte[] shortToBytesLe(short shortValue) {
        byte[] bytes = new byte[2];

        for (int i = 0; i < bytes.length; ++i) {
            bytes[i] = (byte) (shortValue >> i * 8 & 0xFF);
        }

        return bytes;
    }

    public static byte[] shortToBytesBe(short shortValue) {
        byte[] bytes = new byte[2];

        for (int i = 0; i < bytes.length; ++i) {
            bytes[(bytes.length - i - 1)] = (byte) (shortValue >> i * 8 & 0xFF);
        }

        return bytes;
    }

    public static byte[] intToBytesLe(int intValue) {
        byte[] bytes = new byte[4];

        for (int i = 0; i < bytes.length; ++i) {
            bytes[i] = (byte) (intValue >> i * 8 & 0xFF);
        }

        return bytes;
    }

    public static byte[] intToBytesBe(int intValue) {
        byte[] bytes = new byte[4];

        for (int i = 0; i < bytes.length; ++i) {
            bytes[(bytes.length - i - 1)] = (byte) (intValue >> i * 8 & 0xFF);
        }

        return bytes;
    }

    public static int bytesToIntLe(byte[] bytes) {
        if ((bytes == null) || (bytes.length > 4)) {
            throw new RuntimeException("invalid arg");
        }

        int ret = 0;

        for (int i = 0; i < bytes.length; ++i) {
            ret += ((bytes[i] & 0xFF) << i * 8);
        }

        return ret;
    }

    public static int bytesToIntLe(byte[] data, int start, int end) {
        return bytesToIntLe(Arrays.copyOfRange(data, start, end));
    }

    public static int bytesToIntBe(byte[] bytes) {
        if ((bytes == null) || (bytes.length > 4)) {
            throw new RuntimeException("invalid arg");
        }

        int ret = 0;

        for (int i = 0; i < bytes.length; ++i) {
            ret += ((bytes[i] & 0xFF) << (bytes.length - i - 1) * 8);
        }

        return ret;
    }

    public static int bytesToIntBe(byte[] data, int start, int end) {
        return bytesToIntBe(Arrays.copyOfRange(data, start, end));
    }

    public static int bytesToIntLe(byte b0, byte b1, byte b2, byte b3) {
        int ret = 0;

        ret = b0 & 0xFF;
        ret += ((b1 & 0xFF) << 8);
        ret += ((b2 & 0xFF) << 16);
        ret += ((b3 & 0xFF) << 24);

        return ret;
    }

    public static int bytesToIntBe(byte b0, byte b1, byte b2, byte b3) {
        int ret = 0;

        ret = (b0 & 0xFF) << 24;
        ret += ((b1 & 0xFF) << 16);
        ret += ((b2 & 0xFF) << 8);
        ret += (b3 & 0xFF);

        return ret;
    }

    public static void byteArraySetByte(byte[] bytesArray, byte setValue, int index) {
        bytesArray[index] = setValue;
    }

    public static void byteArraySetByte(byte[] bytesArray, int setValue, int index) {
        bytesArray[index] = (byte) (setValue & 0xFF);
    }

    public static void byteArraySetBytes(byte[] bytesArray, byte[] setValues, int index) {
        System.arraycopy(setValues, 0, bytesArray, index, setValues.length);
    }

    public static void byteArraySetWord(byte[] bytesArray, int setValue, int index) {
        bytesArray[index] = (byte) (setValue & 0xFF);
        bytesArray[(index + 1)] = (byte) (setValue >> 8 & 0xFF);
    }

    public static void byteArraySetWordBe(byte[] bytesArray, int setValue, int index) {
        bytesArray[index] = (byte) (setValue >> 8 & 0xFF);
        bytesArray[(index + 1)] = (byte) (setValue & 0xFF);
    }

    public static void byteArraySetInt(byte[] bytesArray, int setValue, int index) {
        bytesArray[index] = (byte) (setValue & 0xFF);
        bytesArray[(index + 1)] = (byte) (setValue >> 8 & 0xFF);
        bytesArray[(index + 2)] = (byte) (setValue >> 16 & 0xFF);
        bytesArray[(index + 3)] = (byte) (setValue >> 24 & 0xFF);
    }

    public static void byteArraySetIntBe(byte[] bytesArray, int setValue, int index) {
        bytesArray[index] = (byte) (setValue >> 24 & 0xFF);
        bytesArray[(index + 1)] = (byte) (setValue >> 16 & 0xFF);
        bytesArray[(index + 2)] = (byte) (setValue >> 8 & 0xFF);
        bytesArray[(index + 3)] = (byte) (setValue & 0xFF);
    }

    public static void delayms(int ms) {
        if (ms <= 0)
            return;
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
        }
    }

    public static Bitmap bitmapRotate(Bitmap b, int degrees) {
        if ((degrees != 0) && (b != null)) {
            Matrix m = new Matrix();
            m.setRotate(degrees, b.getWidth() / 2.0F, b.getHeight() / 2.0F);
            try {
                Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);

                if (b != b2) {
                    b.recycle();
                    b = b2;
                }
            } catch (OutOfMemoryError ex) {
            }
        }
        return b;
    }

    public static boolean isAscii(char ch) {
        return (ch <= ' ');
    }

    public static boolean isAscii(String text) {
        for (int i = 0; i < text.length(); ++i) {
            if (!(isAscii(text.charAt(i)))) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasAsciiChar(String text) {
        for (int i = 0; i < text.length(); ++i) {
            if (isAscii(text.charAt(i))) {
                return true;
            }
        }
        return false;
    }
}
