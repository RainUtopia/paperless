package com.pa.paperless.utils;

import android.graphics.Color;
import android.widget.Button;

import com.google.protobuf.ByteString;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by Administrator on 2017/11/17.
 */

public class MyUtils {

    /**
     * String 转 ByteString
     * @param name
     * @return
     */
    public static ByteString getStb(String name) {
        return ByteString.copyFrom(name, Charset.forName("UTF-8"));
    }

    /**
     * ByteString 转 String
     * @param string
     * @return
     */
    public static String getBts(ByteString string) {
        return new String(string.toByteArray());
    }

    /**
     * 更改按钮选择颜色
     *
     * @param index
     * @param btns
     */
    public static void setViewBackground(int index, List<Button> btns) {
        for (int i = 0; i < btns.size(); i++) {
            if (i == index) {
                btns.get(i).setBackgroundColor(Color.WHITE);
            } else {
                btns.get(i).setBackgroundColor(Color.rgb(216, 216, 216));
            }
        }
    }

    /**
     * 对象转byte
     *
     * @param obj
     * @return
     */
    public static byte[] ObjectToByte(Object obj) {
        byte[] bytes = null;
        try {
            // object to bytearray
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream oo = new ObjectOutputStream(bo);
            oo.writeObject(obj);
            bytes = bo.toByteArray();
            bo.close();
            oo.close();
        } catch (Exception e) {
            System.out.println("translation" + e.getMessage());
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     * byte转对象
     *
     * @param bytes
     * @return
     */
    private Object ByteToObject(byte[] bytes) {
        Object obj = null;
        try {
            // bytearray to object
            ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
            ObjectInputStream oi = new ObjectInputStream(bi);

            obj = oi.readObject();
            bi.close();
            oi.close();
        } catch (Exception e) {
            System.out.println("translation" + e.getMessage());
            e.printStackTrace();
        }
        return obj;
    }
}
