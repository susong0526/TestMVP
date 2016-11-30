package com.example.susong.testmvp.business.main.presenter;

import com.example.susong.testmvp.entity.dto.User;

import java.util.List;

public interface MainInteractor {
    void getUsers(OnGetUserListener listener);

    interface OnGetUserListener {
        void onGetUserStarted();

        void onGetUserFinished(List<User> users);
    }
}
