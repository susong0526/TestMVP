package com.example.susong.testmvp.business.main.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.susong.testmvp.R;
import com.example.susong.testmvp.base.activity.ActivityBaseCompat;
import com.example.susong.testmvp.business.main.presenter.MainPresenter;
import com.example.susong.testmvp.business.main.presenter.MainPresenterFactory;
import com.example.susong.testmvp.business.main.view.MainBaseView;
import com.example.susong.testmvp.entity.po.User;
import com.example.susong.testmvp.framework.PresenterLoader;

import java.util.List;

import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

public class MainActivity extends ActivityBaseCompat implements LoaderManager.LoaderCallbacks<MainPresenter>, MainBaseView {
    private MainPresenter mMainPresenter;
    private ListView mListView;
    private UserAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LoaderManager.getInstance(this).initLoader(100, null, this);
        mListView = findViewById(R.id.list_view);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (null != mMainPresenter) {
            mMainPresenter.setView(this);
            mMainPresenter.queryUsers();
        }
    }

    @Override
    public void showProgress() {
        showLoadingDialog();
    }

    @Override
    public void hideProgress() {
        dissmissLoadingDialog();
    }

    @Override
    public void showError(String url, int code, String message) {
        onRequestError(url, code, message);
    }

    @Override
    public void displayData(List<User> users) {
        if (null == mAdapter) {
            mAdapter = new UserAdapter(users);
            mListView.setAdapter(mAdapter);
        } else {
            mAdapter.setUsers(users);
            mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public Loader<MainPresenter> onCreateLoader(int id, Bundle args) {
        return new PresenterLoader<>(this, new MainPresenterFactory());
    }

    @Override
    public void onLoadFinished(Loader<MainPresenter> loader, MainPresenter data) {
        mMainPresenter = data;
    }

    @Override
    public void onLoaderReset(Loader<MainPresenter> loader) {
        mMainPresenter = null;
    }

    class UserAdapter extends BaseAdapter {
        private List<User> users;

        public UserAdapter(List<User> users) {
            this.users = users;
        }

        @Override
        public int getCount() {
            return users.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            if (null == convertView) {
                TextView textView = new TextView(getApplicationContext());
                textView.setPadding(30, 30, 30, 30);
                textView.setTextColor(Color.BLACK);
                textView.setTextSize(16.0f);

                convertView = textView;
            }
            ((TextView) convertView).setText(users.get(i).getUsername() + "===" + users.get(i).getPwd());
            return convertView;
        }

        public void setUsers(List<User> users) {
            this.users = users;
        }
    }
}
