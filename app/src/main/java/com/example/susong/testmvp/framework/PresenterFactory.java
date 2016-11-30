package com.example.susong.testmvp.framework;

public interface PresenterFactory<T extends Presenter> {
    T create();
}
