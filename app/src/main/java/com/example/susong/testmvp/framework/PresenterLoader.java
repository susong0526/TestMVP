package com.example.susong.testmvp.framework;

import android.content.Context;

import androidx.loader.content.Loader;

public class PresenterLoader<T extends Presenter> extends Loader<T> {
    private final PresenterFactory<T> mPresenterFactory;
    private T mPresenter;

    public PresenterLoader(Context context, PresenterFactory<T> factory) {
        super(context);
        mPresenterFactory = factory;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (null != mPresenter) {
            deliverResult(mPresenter);
        } else {
            forceLoad();
        }
    }

    @Override
    protected void onForceLoad() {
        super.onForceLoad();
        mPresenter = mPresenterFactory.create();
        deliverResult(mPresenter);
    }

    @Override
    protected void onReset() {
        super.onReset();
        mPresenter.onDestroyed();
        mPresenter = null;
    }
}
