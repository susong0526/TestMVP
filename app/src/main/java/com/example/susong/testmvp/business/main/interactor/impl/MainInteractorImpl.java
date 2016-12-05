package com.example.susong.testmvp.business.main.interactor.impl;

import com.example.susong.testmvp.business.main.interactor.MainInteractor;
import com.example.susong.testmvp.business.main.listener.OnGetUserListener;
import com.example.susong.testmvp.business.main.repository.MainRepository;
import com.example.susong.testmvp.business.main.repository.impl.MainRepositoryImpl;

public class MainInteractorImpl implements MainInteractor {
    private MainRepository mMainRepository = new MainRepositoryImpl();

    @Override
    public void getUsers(OnGetUserListener listener) {
        mMainRepository.getUsers(listener);
    }

    @Override
    public void finish() {
        mMainRepository.finish();
        mMainRepository = null;
    }
}
