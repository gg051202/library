package gg.base.library.util;


import android.util.Log;

import com.blankj.utilcode.util.Utils;

import gg.base.library.Constants;

public class LL {

    private LL() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }


    private static final boolean isDebug = Constants.Companion.getSHOW_LOG();
    private static final String TAG = "";

    // 下面四个是默认tag的函数
    public static void i(String msg) {
        if (isDebug)
            print(Log.INFO, TAG, msg);
    }

    public static void d(String msg) {
        if (isDebug)
            print(Log.DEBUG, TAG, msg);
    }

    public static void e(String msg) {
        if (isDebug)
            print(Log.ERROR, TAG, msg);
    }

    public static void v(String msg) {
        if (isDebug)
            print(Log.VERBOSE, TAG, msg);
    }

    // 下面是传入自定义tag的函数
    public static void i(String tag, String msg) {
        if (isDebug)
            print(Log.INFO, tag, msg);
    }

    public static void d(String tag, String msg) {
        if (isDebug)
            print(Log.DEBUG, tag, msg);
    }

    public static void e(String tag, String msg) {
        if (isDebug)
            print(Log.ERROR, tag, msg);
    }

    public static void v(String tag, String msg) {
        if (isDebug)
            print(Log.VERBOSE, tag, msg);
    }

    private static void print(int type, String tagName, String msg) {
        if (msg == null) {
            msg = "你要打印的内容是null";
        }
        if (msg.equals("")) {
            msg = "你要打印的内容是空字符串";
        }
        if (Constants.Companion.isShowLogButton()) {
            LocalLogUtil.writeLogtoFile(Utils.getApp(), tagName, msg);
        }
        int strLength = msg.length();
        int start = 0;
        int LOG_MAXLENGTH = 2000;
        int end = LOG_MAXLENGTH;
        for (int i = 0; i < 100; i++) {
            if (strLength > end) {
                print2(type, tagName + (i == 0 ? "" : "████████████████████████"), msg.substring(start, end));
                start = end;
                end = end + LOG_MAXLENGTH;
            } else {
                print2(type, tagName + (i == 0 ? "" : "████████████████████████"), msg.substring(start, strLength));
                break;
            }
        }
    }

    private static void print2(int type, String tag, String value) {
        tag = "AndroidRuntime_leo" + tag;
        if (type == Log.INFO) {
            Log.i(tag, value);
        } else if (type == Log.DEBUG) {
            Log.d(tag, value);
        } else if (type == Log.ERROR) {
            Log.e(tag, value);
        } else if (type == Log.VERBOSE) {
            Log.v(tag, value);
        }
    }


}