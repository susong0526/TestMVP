package com.example.susong.testmvp.business.login.view;


import com.example.susong.testmvp.framework.View;

public interface LoginView extends View {
    void onLoginStarted();

    void onLoginSuccess();

    void onLoginError(String error);

    void pushToHome();
}
