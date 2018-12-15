package com.example.susong.testmvp.business.login.view;


import com.example.susong.testmvp.framework.BaseView;

public interface LoginBaseView extends BaseView {
    void onLoginStarted();

    void onLoginSuccess();

    void onLoginError(String error);

    void pushToHome();
}
