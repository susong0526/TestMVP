package com.example.susong.testmvp.util;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.susong.testmvp.base.A;

public class ToastUtil {
    private static ToastUtil mInstance;
    private AlertDialog mDialog;
    private Context mPrevContext;
    private Toast mToast;
    private Handler mHandler = new Handler();

    private Runnable r = new Runnable() {

        public void run() {
            mToast.cancel();
        }
    };

    private ToastUtil() {
    }

    public static ToastUtil getInstance() {
        if (null == mInstance) {
            mInstance = new ToastUtil();
        }
        return mInstance;
    }

    public void show() {

    }

    public void showDialog(Context context, String text) {
        if (null == context || TextUtils.isEmpty(text)) return;
        if (context instanceof Activity) {
            Activity activityBase = (Activity) context;
            if (!activityBase.isFinishing()) {
                if (mPrevContext != context) {
                    mDialog = null;
                }
                mPrevContext = context;
                if (null == mDialog) {
                    mDialog = new AlertDialog.Builder(context).setCancelable(true).create();
                }
                mDialog.setMessage(text);
                if (!mDialog.isShowing()) mDialog.show();
            }
        }
    }

    public void showToast(Context context, int text) {
        String message = null;
        if (context != null) {
            message = context.getString(text);
        }
        showToast(context, message, Toast.LENGTH_LONG);
    }

    public void showToast(Context context, String text) {
        showToast(context, text, Toast.LENGTH_LONG);
    }

    public void showToast(Context context, int text, int duration) {
        String message = null;
        if (context != null) {
            message = context.getString(text);
        }
        showToast(context, message, duration);
    }

    public void showToast(Context context, String text, int duration) {
        if (null == context || TextUtils.isEmpty(text)) return;
        mHandler.removeCallbacks(r);
        if (mToast != null) {
            mToast.setText(text);
        } else {
            mToast = Toast.makeText(A.instance, text, Toast.LENGTH_SHORT);
        }
        int tempDuration = 3000;
        if (duration == Toast.LENGTH_SHORT) {
            tempDuration = 2000;
        } else if (duration == Toast.LENGTH_LONG) {
            tempDuration = 3500;
        }
        mHandler.postDelayed(r, tempDuration);
        mToast.show();
    }
}
