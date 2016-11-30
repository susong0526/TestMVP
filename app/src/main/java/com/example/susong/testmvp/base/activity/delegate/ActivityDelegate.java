package com.example.susong.testmvp.base.activity.delegate;

import android.os.Bundle;

/**
 * Activity生命周期代理类
 */
public interface ActivityDelegate {
    void onCreate(Bundle savedInstanceState);

    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void onActivityDestroy();
}
