package com.example.susong.testmvp.base.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.ColorRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.example.susong.testmvp.base.activity.delegate.ActivityDelegate;
import com.example.susong.testmvp.base.activity.manager.ActivityManager;
import com.example.susong.testmvp.http.HttpDataApi;
import com.example.susong.testmvp.util.HandlerManager;
import com.example.susong.testmvp.widget.AbsToolbar;
import com.example.susong.testmvp.widget.LoadingDialog;

import org.apache.http.HttpStatus;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * 简化ActivityBase逻辑,新版Activity请继承自该类
 * Note: 请不要随意在ActivityBase类中添加逻辑,如需添加
 * 请慎重思考,无法取舍之下甚至可以 @Scott
 */
public abstract class ActivityBaseCompat extends AppCompatActivity implements AbsToolbar.OnNavigationClickListener, HttpDataApi.OnRequestCallback {
    private LinearLayout mContentView;
    protected AbsToolbar mToolbar;
    private ArrayList<ActivityDelegate> mDelegates = new ArrayList<>();
    private Toolbar.OnMenuItemClickListener onMenuItemClickListener;
    private PopupWindow mNetworkStatusBar;
    private boolean mActivityLoadingFinished = false;
    private HandlerManager mMainHandler = new HandlerManager(this);
    private Runnable mCheckStatusTask;
    // 用于页面显示前的Loading页面
    private FrameLayout mLoadingLayout;
    // 用于显示 Loading Dialog
    public LoadingDialog mDialog;
    protected OnBackPressedListener onBackPressedListener;

    public interface OnBackPressedListener {
        boolean onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        if (mDelegates.size() > 0) {
            for (ActivityDelegate delegate : mDelegates) {
                delegate.onCreate(savedInstanceState);
            }
        }
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        ActivityManager.newInstance().push(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mDelegates.size() > 0) {
            for (ActivityDelegate delegate : mDelegates) {
                delegate.onStart();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDelegates.size() > 0) {
            for (ActivityDelegate delegate : mDelegates) {
                delegate.onResume();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mDelegates.size() > 0) {
            for (ActivityDelegate delegate : mDelegates) {
                delegate.onPause();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mDelegates.size() > 0) {
            for (ActivityDelegate delegate : mDelegates) {
                delegate.onStop();
            }
        }
        if (null != mNetworkStatusBar) {
            mNetworkStatusBar.dismiss();
            mNetworkStatusBar = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDelegates.size() > 0) {
            for (ActivityDelegate delegate : mDelegates) {
                delegate.onActivityDestroy();
            }
        }
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        if (null != mCheckStatusTask) {
            mMainHandler.removeCallbacks(mCheckStatusTask);
        }
        MemoryCache.sharedInstance().clear(getClass().getName());
        ActivityManager.newInstance().remove(this);
        loadingFinished();
        dissmissLoadingDialog();
    }

    private void initView() {
        mContentView = new LinearLayout(this);
        mContentView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mContentView.setOrientation(LinearLayout.VERTICAL);
        mToolbar = toolbar();
        if (null != mToolbar) {
            ViewGroup.LayoutParams layoutParams = mToolbar.getLayoutParams();
            if (null == layoutParams) {
                layoutParams = generateDefaultLayoutParams();
            }
            mToolbar.setOnNavigationClickListener(this);
            mContentView.addView(mToolbar, layoutParams);
        }
        super.setContentView(mContentView);
    }

    /**
     * 显示加载页面
     *
     * @return 当前的对话框对象
     */
    public void showLoadingDialog() {
        showLoadingDialog(getString(R.string.loading));
    }

    /**
     * 显示加载页面
     *
     * @param loadingText 加载提示文字
     * @return 当前的对话框对象
     */
    public void showLoadingDialog(String loadingText) {
        if (mDialog == null) {
            mDialog = new LoadingDialog(this);
        }
        mDialog.setTitle(loadingText);
        if (mDialog.isShowing()) {
            mDialog.dismiss();
        }
        mDialog.show();
    }

    public void dissmissLoadingDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    public void loadingStarted() {
        if (null == mLoadingLayout) {
            mLoadingLayout = new FrameLayout(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            lp.gravity = Gravity.CENTER;
            mLoadingLayout.setLayoutParams(lp);
            getLayoutInflater().inflate(R.layout.progress_bar_new, mLoadingLayout);
        }
        for (int i = 0; i < mContentView.getChildCount(); i++) {
            if (mContentView.getChildAt(i) != mToolbar) {
                mContentView.getChildAt(i).setVisibility(View.GONE);
            }
        }
        if (mContentView.indexOfChild(mLoadingLayout) == -1) {
            mLoadingLayout.setVisibility(View.VISIBLE);
            mContentView.addView(mLoadingLayout);
        }
    }

    public void loadingFinished() {
        if (null != mLoadingLayout && mContentView.indexOfChild(mLoadingLayout) != -1) {
            mContentView.removeView(mLoadingLayout);
            for (int i = 0; i < mContentView.getChildCount(); i++) {
                mContentView.getChildAt(i).setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        getLayoutInflater().inflate(layoutResID, mContentView);
    }

    @Override
    public void setContentView(View view) {
        setContentView(view, view.getLayoutParams());
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        if (null == params) {
            params = generateDefaultLayoutParams();
        }
        if (mContentView.indexOfChild(view) == -1) {
            mContentView.addView(view, params);
        }
    }

    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void setTitle(CharSequence title) {
        if (null != mToolbar) {
            mToolbar.setTitle(title);
        }
    }

    public void setTitleClickListener(View.OnClickListener listener) {
        if (null != mToolbar) {
            mToolbar.setTitleClickListener(listener);
        }
    }

    public void setNavigationDrawable(int drawableId) {
        if (null != mToolbar) {
            mToolbar.setNavigationDrawable(drawableId);
        }
    }

    public void setTitleColor(int colorId) {
        if (null != mToolbar) {
            mToolbar.setTitleColor(colorId);
        }
    }

    public void setToolbarBackgroundColor(@ColorRes int colorId) {
        if (null != mToolbar) {
            mToolbar.setBackgroundColor(getResources().getColor(colorId));
        }
    }

    public void inflateMenu(@MenuRes int menuId) {
        clearMenu();
        if (null != mToolbar) {
            mToolbar.inflateMenu(menuId);
            mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (null != onMenuItemClickListener) {
                        onMenuItemClickListener.onMenuItemClick(item);
                    }
                    ActivityBaseCompat.this.onMenuItemClick(item);
                    return true;
                }
            });
            // Ugly code... But i can't help...
            Menu menu = mToolbar.getMenu();
            if (null != menu) {
                for (int i = 0; i < menu.size(); i++) {
                    MenuItem item = menu.getItem(i);
                    SpannableString text = new SpannableString(item.getTitle() + "");
                    text.setSpan(new ForegroundColorSpan(Color.WHITE), 0, text.length(), 0);
                    item.setTitle(text);
                }
            }
        }
    }

    public void clearMenu() {
        if (null != mToolbar) {
            mToolbar.getMenu().clear();
        }
    }

    /**
     * 获取Toolbar实例,该方法可以被重写
     *
     * @return
     */
    public AbsToolbar toolbar() {
        if (null == mToolbar) {
            mToolbar = new DefaultToolbar(this);
        }
        return mToolbar;
    }

    @Subscribe
    public void onEvent(NetworkChangeEvent event) {
        switch (event.getStatus()) {
            case NetworkChangeEvent.CONNECTED: {
                displayNetworkStatusBar(false);
                break;
            }
            case NetworkChangeEvent.DISCONNECTED: {
                displayNetworkStatusBar(true);
                break;
            }
        }
    }

    private void displayNetworkStatusBar(boolean display) {
        if (mActivityLoadingFinished) {
            if (display) {
                if (null == mNetworkStatusBar) {
                    mNetworkStatusBar = new PopupWindow(this);
                    View contentView = getLayoutInflater().inflate(R.layout.fl_warning_view, null);
                    contentView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                                startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    mNetworkStatusBar.setContentView(contentView);
                    mNetworkStatusBar.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                    mNetworkStatusBar.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                    mNetworkStatusBar.setBackgroundDrawable(null);
                }
                if (null != mToolbar) {
                    mNetworkStatusBar.showAsDropDown(mToolbar);
                    MemoryCache.sharedInstance().put(getClass().getName(), true);
                }
            } else {
                if (null == mNetworkStatusBar) return;
                mNetworkStatusBar.dismiss();
                MemoryCache.sharedInstance().put(getClass().getName(), false);
            }
        }
    }

    /**
     * 设置代理类,处理生命周期等相关触发方法
     *
     * @param delegate
     */
    public void addDelegate(ActivityDelegate delegate) {
        if (null != delegate && !mDelegates.contains(delegate)) {
            mDelegates.add(delegate);
        }
    }

    @Override
    public void onNavigationClick(View view) {
        finish();
    }

    public void setOnMenuItemClickListener(final Toolbar.OnMenuItemClickListener onMenuItemClickListener) {
        this.onMenuItemClickListener = onMenuItemClickListener;
    }

    protected void onMenuItemClick(MenuItem item) {
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mActivityLoadingFinished = hasFocus;
        if (mActivityLoadingFinished) {
            Boolean display = (Boolean) MemoryCache.sharedInstance().get(getClass().getName());
            if (null != display && display) {
                if (null == mCheckStatusTask) {
                    mCheckStatusTask = new Runnable() {
                        @Override
                        public void run() {
                            displayNetworkStatusBar(true);
                        }
                    };
                }
                mMainHandler.removeCallbacks(mCheckStatusTask);
                mMainHandler.postDelayed(mCheckStatusTask, 200);
            }
        }
    }

    @Override
    public void onRequestSuccess(String url, ResObj result, boolean isFrmCache) {
        if (Constants.CODE_SUCCESS != result.getCode()
                && Constants.MainPageUserShopType.NO_SHOP != result.getCode()
                && Constants.MainPageUserShopType.EXIST_SHOP != result.getCode()) {
            doRequestError(url, HttpStatus.SC_OK, result.getMsg());
        }
    }

    public void doRequestError(String url, int code, String message) {
        if (HttpStatus.SC_OK == code) {
            if (onNeedCallSuper(url)) {
                dissmissLoadingDialog();

                if (TextUtils.isEmpty(message)) {
                    message = getString(R.string.server_busy_retry_later);
                }
                ToastUtil.getInstance().showDialog(this, message);
            }
            // 会话过期错误码特殊处理
            if (ResObj.CODE_INVALID_TOKEN == code || ResObj.CODE_TOKEN_EXPIRED == code || ResObj.CODE_COMPLETE_INFO == code) {
                SessionUtil.loginOut();
                Intent intent = new Intent(this, ActivityLogin.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(ActivityLogin.BEING_OPENED_FRAGMENT_ID, Constants.FRAGMENT_IDS.LOGIN);
                startActivity(intent);
            }
        } else {
            dissmissLoadingDialog();

            String msg = message;
            switch (code) {
                case HttpDataApi.OnRequestCallback.CODE_NETWORK_TIME_OUT: {
                    msg = getString(R.string.tip_network_time_out);
                    break;
                }
                case HttpDataApi.OnRequestCallback.CODE_AUTH_FAILURE:
                case HttpDataApi.OnRequestCallback.CODE_REDIRECT_ERROR:
                case HttpDataApi.OnRequestCallback.CODE_SERVER_ERROR: {
                    msg = getString(R.string.server_busy_retry_later);
                    break;
                }
                case HttpDataApi.OnRequestCallback.CODE_NETWORK_ERROR: {
                    msg = getString(R.string.tip_network_error);
                    break;
                }
                case HttpStatus.SC_INTERNAL_SERVER_ERROR:
                    if (TextUtils.isEmpty(msg)) {
                        msg = getString(R.string.server_busy_retry_later);
                    }
                    break;
            }
            ToastUtil.getInstance().showDialog(this, msg);
        }
    }

    @Override
    public void onRequestError(String url, int statusCode, String error) {
        doRequestError(url, statusCode, error);
    }

    /**
     * 如果不需要使用父类提示,请重写该方法,requestTag用于兼容HttpUtils类,新版请求请使用DataLoader
     *
     * @param url URL
     * @return
     */
    protected boolean onNeedCallSuper(String url) {
        return true;
    }

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }

    @Override
    public void onBackPressed() {
        if(null != onBackPressedListener && onBackPressedListener.onBackPressed()) return;
        super.onBackPressed();
    }
}
