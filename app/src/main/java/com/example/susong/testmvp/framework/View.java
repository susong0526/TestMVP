package com.example.susong.testmvp.framework;

public interface View {
    void showProgress();

    void hideProgress();

    void showError(String url, int code, String message);
}
