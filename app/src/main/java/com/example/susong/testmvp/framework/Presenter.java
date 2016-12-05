package com.example.susong.testmvp.framework;

public interface Presenter<V extends View> {
    void onViewAttached(V view);

    void onViewDetached();

    void onDestroyed();

    void setView(V view);
}
