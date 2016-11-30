package com.example.susong.testmvp.util;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import java.lang.ref.WeakReference;

/**
 * 可以避免内存泄露的Handler
 */
public class HandlerManager extends Handler {
    private WeakReference<AppCompatActivity> reference;

    public HandlerManager(AppCompatActivity activity) {
        reference = new WeakReference<>(activity);
    }

    public AppCompatActivity getActivity() {
        return reference.get();
    }
}
