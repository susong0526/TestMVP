package com.example.susong.testmvp.business.main.interactor;

import com.example.susong.testmvp.business.main.listener.OnGetUserListener;
import com.example.susong.testmvp.framework.Interactor;

public interface MainInteractor extends Interactor {
    void getUsers(OnGetUserListener listener);
}
