package com.example.susong.testmvp.business.login.presenter;

import com.example.susong.testmvp.framework.PresenterFactory;

public class LoginPresneterFactory implements PresenterFactory<LoginPresenter> {
    @Override
    public LoginPresenter create() {
        return new LoginPresenter();
    }
}
