package com.example.susong.testmvp.business.main.presenter;

import android.os.Handler;

import com.example.susong.testmvp.business.login.model.UserRepository;
import com.example.susong.testmvp.business.login.model.UserSpecification;
import com.example.susong.testmvp.database.Repository;
import com.example.susong.testmvp.entity.dto.User;

import java.util.List;

public class MainInteractorImpl implements MainInteractor {
    private Repository<User> mUserRepository = new UserRepository();
    private Handler mHandler = new Handler();

    @Override
    public void getUsers(final OnGetUserListener listener) {
        if (null != listener) listener.onGetUserStarted();
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                final List<User> users = mUserRepository.query(new UserSpecification());
                if (null != listener) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onGetUserFinished(users);
                        }
                    });
                }
            }
        }.start();
    }
}
