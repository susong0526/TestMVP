package com.example.susong.testmvp.business.login.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.susong.testmvp.R;
import com.example.susong.testmvp.base.activity.ActivityBaseCompat;
import com.example.susong.testmvp.business.login.presenter.LoginPresenter;
import com.example.susong.testmvp.business.login.presenter.LoginPresneterFactory;
import com.example.susong.testmvp.business.login.view.LoginBaseView;
import com.example.susong.testmvp.business.main.activity.MainActivity;
import com.example.susong.testmvp.framework.PresenterLoader;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

public class LoginActivity extends ActivityBaseCompat implements LoaderManager.LoaderCallbacks<LoginPresenter>, LoginBaseView {

    private LoginPresenter mPresenter;
    private final int LOADER_ID = 100;
    private EditText mUsernameEdit;
    private EditText mPwdEdit;
    private ProgressDialog mDialog;
    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
        Button mLoginButton = (Button) findViewById(R.id.button_login);
        mUsernameEdit = (EditText) findViewById(R.id.edit_username);
        mPwdEdit = (EditText) findViewById(R.id.edit_pwd);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.login(mUsernameEdit.getText().toString(), mPwdEdit.getText().toString());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (null != mPresenter) mPresenter.setView(this);
    }

    @Override
    public Loader<LoginPresenter> onCreateLoader(int id, Bundle args) {
        return new PresenterLoader<>(this, new LoginPresneterFactory());
    }

    @Override
    public void onLoadFinished(Loader<LoginPresenter> loader, LoginPresenter data) {
        mPresenter = data;
    }

    @Override
    public void onLoaderReset(Loader<LoginPresenter> loader) {
        mPresenter = null;
    }

    @Override
    public void onLoginStarted() {
        showProgress();
    }

    @Override
    public void onLoginSuccess() {
        hideProgress();
        pushToHome();
    }

    @Override
    public void onLoginError(String error) {
        hideProgress();
        if (null == mAlertDialog) {
            mAlertDialog = new AlertDialog.Builder(this).setMessage(error).create();
        }
        if (!mAlertDialog.isShowing()) mAlertDialog.show();
    }

    @Override
    public void showProgress() {
        if (null == mDialog) {
            mDialog = new ProgressDialog(this);
        }
        if (!mDialog.isShowing()) mDialog.show();
    }

    @Override
    public void hideProgress() {
        if (null != mDialog && mDialog.isShowing()) mDialog.dismiss();
    }

    @Override
    public void showError(String url, int code, String message) {
        onRequestError(url, code, message);
    }

    @Override
    public void pushToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
