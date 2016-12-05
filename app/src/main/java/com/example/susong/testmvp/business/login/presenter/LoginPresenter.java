package com.example.susong.testmvp.business.login.presenter;

import android.os.Handler;

import com.example.susong.testmvp.business.login.view.LoginView;
import com.example.susong.testmvp.database.Repository;
import com.example.susong.testmvp.business.login.model.UserRepository;
import com.example.susong.testmvp.business.login.model.UserSpecification;
import com.example.susong.testmvp.entity.po.User;
import com.example.susong.testmvp.framework.Presenter;
import com.example.susong.testmvp.framework.View;

import java.util.List;

public class LoginPresenter implements Presenter<LoginView> {
    private Handler mHandler = new Handler();
    private Repository<User> mUserRepository = new UserRepository();
    private LoginView mLoginView;

    @Override
    public void onViewAttached(LoginView view) {

    }

    @Override
    public void onViewDetached() {

    }

    @Override
    public void onDestroyed() {
        
    }

    @Override
    public void setView(View view) {
        mLoginView = (LoginView) view;
    }

    public void login(final String username, final String pwd) {
        if (null != mLoginView) mLoginView.onLoginStarted();
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                List<User> users = mUserRepository.query(new UserSpecification());
                if (users.size() > 0) {
                    if (users.get(0).getUsername().equals(username) && users.get(0).getPwd().equals(pwd)) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (null != mLoginView) {
                                    mLoginView.onLoginSuccess();
                                }
                            }
                        });
                        return;
                    }
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (null != mLoginView) {
                            mLoginView.onLoginError("用户名或密码错误!!!");
                        }
                    }
                });
            }
        }.start();

    }
}
