package com.example.susong.testmvp.business.main.presenter;


import com.example.susong.testmvp.business.main.view.MainView;
import com.example.susong.testmvp.entity.dto.User;
import com.example.susong.testmvp.framework.Presenter;
import com.example.susong.testmvp.framework.View;

import java.util.List;

public class MainPresenter implements Presenter<MainView>, MainInteractor.OnGetUserListener {
    private MainInteractor interactor = new MainInteractorImpl();
    private MainView mainView;

    @Override
    public void onViewAttached(MainView view) {

    }

    @Override
    public void onViewDetached() {

    }

    @Override
    public void onDestroyed() {

    }

    @Override
    public void setView(View view) {
        mainView = (MainView) view;
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
}
