package com.example.susong.testmvp.business.main.presenter;

import com.example.susong.testmvp.framework.PresenterFactory;

public class MainPresenterFactory implements PresenterFactory<MainPresenter> {
    @Override
    public MainPresenter create() {
        return new MainPresenter();
    }
}
