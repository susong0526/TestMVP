package com.example.susong.testmvp.framework;

public interface Presenter<V extends BaseView> {

    void onViewAttached(V view);

    void onViewDetached();

    void onDestroyed();

    void setView(V view);
}
