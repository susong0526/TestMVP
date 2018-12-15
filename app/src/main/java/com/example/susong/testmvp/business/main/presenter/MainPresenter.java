package com.example.susong.testmvp.business.main.presenter;


import com.example.susong.testmvp.business.main.interactor.MainInteractor;
import com.example.susong.testmvp.business.main.interactor.impl.MainInteractorImpl;
import com.example.susong.testmvp.business.main.listener.OnGetUserListener;
import com.example.susong.testmvp.business.main.view.MainBaseView;
import com.example.susong.testmvp.entity.po.User;
import com.example.susong.testmvp.framework.Presenter;

import java.util.List;

public class MainPresenter implements Presenter<MainBaseView>, OnGetUserListener {
    private MainInteractor interactor = new MainInteractorImpl();
    private MainBaseView mainView;

    @Override
    public void onViewAttached(MainBaseView view) {
    }

    @Override
    public void onViewDetached() {
    }

    @Override
    public void onDestroyed() {
        interactor.finish();
        interactor = null;
        mainView = null;
    }

    @Override
    public void setView(MainBaseView view) {
        mainView = view;
    }

    public void queryUsers() {
        interactor.getUsers(this);
    }

    @Override
    public void onGetUserStarted() {
        mainView.showProgress();
    }

    @Override
    public void onGetUserFinished(List<User> users) {
        mainView.hideProgress();
        mainView.displayData(users);
    }

    @Override
    public void showError(String url, int code, String message) {
        mainView.showError(url, code, message);
    }
}
