package com.example.susong.testmvp.base.activity.manager;

import com.example.susong.testmvp.base.activity.ActivityBaseCompat;

public abstract class ActivityManager {
    private static ActivityManager instance;

    public static ActivityManager newInstance() {
        if (null == instance) {
            instance = new ActivityManagerImpl();
        }
        return instance;
    }

    public abstract void push(ActivityBaseCompat activity);

    public abstract void pop();

    public abstract void pop(Class<? extends ActivityBaseCompat> cls);

    public abstract void popAtIndex(int index);

    public abstract void remove(ActivityBaseCompat activity);
}
