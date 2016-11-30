package com.example.susong.testmvp.base.activity.manager;

import com.example.susong.testmvp.base.activity.ActivityBaseCompat;

import java.util.ArrayList;

public class ActivityManagerImpl extends ActivityManager {
    private ArrayList<ActivityBaseCompat> activityStack = new ArrayList<>(3);
    private final int ACTIVITY_MAX_NUMBER = 10;

    @Override
    public void push(ActivityBaseCompat activity) {
        if (activityStack.size() >= ACTIVITY_MAX_NUMBER) {
            ActivityBaseCompat activityBaseCompat = activityStack.remove(activityStack.size() - 1);
            if (null != activityBaseCompat && !activityBaseCompat.isFinishing()) {
                activityBaseCompat.finish();
            }
        }
        activityStack.add(0, activity);
    }

    @Override
    public void pop() {
        popAtIndex(0);
    }

    @Override
    public void pop(Class<? extends ActivityBaseCompat> cls) {
        if (null != cls && !activityStack.isEmpty()) {
            for (ActivityBaseCompat activity : activityStack) {
                if (null != activity && activity.getClass().getName().equals(cls.getName())) {
                    activityStack.remove(activity);
                    activity.finish();
                    break;
                }
            }
        }
    }

    @Override
    public void popAtIndex(int index) {
        if (activityStack.size() > index) {
            ActivityBaseCompat activityBaseCompat = activityStack.remove(index);
            if (null != activityBaseCompat && !activityBaseCompat.isFinishing()) {
                activityBaseCompat.finish();
            }
        }
    }

    @Override
    public void remove(ActivityBaseCompat activity) {
        if (null != activity) {
            for (ActivityBaseCompat activityBaseCompat : activityStack) {
                if (null != activityBaseCompat && activityBaseCompat == activity) {
                    activityStack.remove(activityBaseCompat);
                    break;
                }
            }
        }
    }
}
