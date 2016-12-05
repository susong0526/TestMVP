package com.example.susong.testmvp.business.main.repository.impl;

import com.example.susong.testmvp.business.main.listener.OnGetUserListener;
import com.example.susong.testmvp.business.main.repository.MainRepository;
import com.example.susong.testmvp.entity.dto.request.ReqObj;
import com.example.susong.testmvp.entity.dto.response.ResObj;
import com.example.susong.testmvp.entity.po.User;
import com.example.susong.testmvp.http.HttpDataApi;
import com.example.susong.testmvp.http.HttpDataLoader;
import com.example.susong.testmvp.http.HttpDataReqUrl;

import org.apache.http.HttpStatus;

import java.util.List;

public class MainRepositoryImpl implements MainRepository, HttpDataApi.OnRequestCallback {

    private HttpDataLoader mHttpDataLoader;
    private OnGetUserListener mListener;

    public MainRepositoryImpl() {
        mHttpDataLoader = new HttpDataLoader(this);
    }

    @Override
    public void getUsers(OnGetUserListener listener) {
        mListener = listener;
        listener.onGetUserStarted();
        mHttpDataLoader.GET(HttpDataReqUrl.URL_CALCULATE_WITHDRAW_MONTY, new ReqObj());
    }

    @Override
    public void finish() {
        mHttpDataLoader.finish();
        mHttpDataLoader = null;
        mListener = null;
    }

    @Override
    public void onRequestSuccess(String url, ResObj result, boolean isFrmCache) {
        mListener.showError(url, HttpStatus.SC_OK, result.getMsg());
        mListener.onGetUserFinished((List<User>) result.getData());
    }

    @Override
    public void onRequestError(String url, int statusCode, String error) {
        mListener.showError(url, statusCode, error);
    }
}
