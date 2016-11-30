package com.example.susong.testmvp.util.Log;

import android.util.Log;

import com.example.susong.testmvp.C;
import com.orhanobut.logger.LogTool;

public class LoggerTool implements LogTool {

    public void d(String tag, String message) {
        if (C.PRINT_LOG) {
            Log.d(tag, message);
        }
    }

    public void e(String tag, String message) {
        if (C.PRINT_LOG) {
            Log.e(tag, message);
        }
    }

    public void w(String tag, String message) {
        if (C.PRINT_LOG) {
            Log.w(tag, message);
        }
    }

    public void i(String tag, String message) {
        if (C.PRINT_LOG) {
            Log.i(tag, message);
        }
    }

    public void v(String tag, String message) {
        if (C.PRINT_LOG) {
            Log.v(tag, message);
        }
    }

    public void wtf(String tag, String message) {
        if (C.PRINT_LOG) {
            Log.wtf(tag, message);
        }
    }
}
