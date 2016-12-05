package com.example.susong.testmvp.business.main.listener;

import com.example.susong.testmvp.entity.po.User;
import com.example.susong.testmvp.framework.Listener;

import java.util.List;

public interface OnGetUserListener extends Listener {
    void onGetUserStarted();

    void onGetUserFinished(List<User> users);
}
