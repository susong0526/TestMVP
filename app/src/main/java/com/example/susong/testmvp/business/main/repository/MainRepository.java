package com.example.susong.testmvp.business.main.repository;

import com.example.susong.testmvp.business.main.listener.OnGetUserListener;
import com.example.susong.testmvp.framework.Repository;

public interface MainRepository extends Repository {
    void getUsers(OnGetUserListener listener);
}
