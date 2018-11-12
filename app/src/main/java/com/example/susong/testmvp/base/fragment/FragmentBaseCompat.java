package com.example.susong.testmvp.base.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.AnimRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.example.susong.testmvp.R;
import com.example.susong.testmvp.base.activity.ActivityBaseCompat;
import com.example.susong.testmvp.base.activity.FragmentActivityBase;
import com.example.susong.testmvp.base.fragment.delegate.FragmentDelegate;
import com.example.susong.testmvp.entity.dto.response.ResObj;
import com.example.susong.testmvp.http.HttpDataApi;

import org.apache.http.HttpStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 简化FragmentBase逻辑
 * Note: 请注意不要轻易在FragmentBase类中添加逻辑,如需添加,请慎重思考
 */
public abstract class FragmentBaseCompat extends Fragment implements OnFragmentResultListener, HttpDataApi.OnRequestCallback {
    private FragmentBaseCompat last;
    private FragmentBaseCompat nextNode;
    private ArrayList<FragmentDelegate> mDelegates = new ArrayList<>();
    /**
     * 页面标题
     */
    private CharSequence mTitle;
    /**
     * 返回键按钮图片
     */
    private int mNavigationDrawable;
    /**
     * 页面标题颜色
     */
    private int mTitleColor;
    /**
     * Toolbar背景色
     */
    private int mToolbarBackgroundColor;
    /**
     * Toolbar菜单栏
     */
    private int mToolbarMenu;
    /**
     * 设置跳转至该页面的操作方式
     */
    public static final int OPER_PUSH = 1;
    public static final int OPER_BACK = 2;
    private int mCurrOper;
    private OnFragmentResultListener onFragmentResultListener;
    /**
     * Request Code
     */
    public static final String KEY_REQUEST_CODE = "___request_code___";
    /**
     * Result Code
     */
    public static final int RESULT_OK = ActivityBaseCompat.RESULT_OK;
    public static final int RESULT_CANCEL = ActivityBaseCompat.RESULT_CANCELED;
    public static final int RESULT_FIRST_USER = ActivityBaseCompat.RESULT_FIRST_USER;
    private int mResultCode = RESULT_CANCEL;
    /**
     * Result Data
     */
    private Bundle mResultData;
    /**
     * 是否调用了setResult方法
     */
    private boolean mCalledSetResult = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (mDelegates.size() > 0) {
            for (FragmentDelegate delegate : mDelegates) {
                delegate.onAttach(context);
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mDelegates.size() > 0) {
            for (FragmentDelegate delegate : mDelegates) {
                delegate.onCreate(savedInstanceState);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mDelegates.size() > 0) {
            for (FragmentDelegate delegate : mDelegates) {
                delegate.onCreateView(inflater, container, savedInstanceState);
            }
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mDelegates.size() > 0) {
            for (FragmentDelegate delegate : mDelegates) {
                delegate.onActivityCreate(savedInstanceState);
            }
        }
        if (!needSetItemClick()) {
            if (getActivity() instanceof ActivityBaseCompat) {
                ((ActivityBaseCompat) getActivity()).setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        FragmentBaseCompat.this.onMenuItemClick(item);
                        return true;
                    }
                });
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mDelegates.size() > 0) {
            for (FragmentDelegate delegate : mDelegates) {
                delegate.onStart();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (onNeedRefresh() && !isHidden()) {
            onDataLoad();
            mCurrOper = OPER_BACK;
        }
        if (getActivity() instanceof ActivityBaseCompat && needUpdateToolbar()) {
            if (0 != mNavigationDrawable) {
                ((ActivityBaseCompat) getActivity()).setNavigationDrawable(mNavigationDrawable);
            } else {
                ((ActivityBaseCompat) getActivity()).setNavigationDrawable(R.drawable.return_btn);
            }
            if (!TextUtils.isEmpty(mTitle)) {
                getActivity().setTitle(mTitle);
            } else {
                getActivity().setTitle("");
            }
            if (0 != mTitleColor) {
                getActivity().setTitleColor(mTitleColor);
            } else {
                getActivity().setTitleColor(R.color.white);
            }
            if (0 != mToolbarBackgroundColor) {
                ((ActivityBaseCompat) getActivity()).setToolbarBackgroundColor(mToolbarBackgroundColor);
            } else {
                ((ActivityBaseCompat) getActivity()).setToolbarBackgroundColor(R.color.toolbar_background_color);
            }
            if (0 != mToolbarMenu) {
                ((ActivityBaseCompat) getActivity()).inflateMenu(mToolbarMenu);
            } else {
                ((ActivityBaseCompat) getActivity()).clearMenu();
            }
        }
        if (mDelegates.size() > 0) {
            for (FragmentDelegate delegate : mDelegates) {
                delegate.onResume();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mDelegates.size() > 0) {
            for (FragmentDelegate delegate : mDelegates) {
                delegate.onPause();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mDelegates.size() > 0) {
            for (FragmentDelegate delegate : mDelegates) {
                delegate.onStop();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mDelegates.size() > 0) {
            for (FragmentDelegate delegate : mDelegates) {
                delegate.onDestroyView();
            }
        }
        loadingFinished();
        dissmissLoadingDialog();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDelegates.size() > 0) {
            for (FragmentDelegate delegate : mDelegates) {
                delegate.onFragmentDestroy();
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mDelegates.size() > 0) {
            for (FragmentDelegate delegate : mDelegates) {
                delegate.onDetach();
            }
        }
    }

    public FragmentBaseCompat() {
    }

    public void setTitle(CharSequence title) {
        mTitle = title;
    }

    public void setTitle(@StringRes int resId) {
        setTitle(getString(resId));
    }

    public void setTitleColor(int colorId) {
        mTitleColor = colorId;
    }

    public void setNavigationDrawable(int drawableId) {
        mNavigationDrawable = drawableId;
    }

    public void setToolbarBackgroundColor(int colorId) {
        mToolbarBackgroundColor = colorId;
    }

    public void setToolbarMenu(int menuId) {
        mToolbarMenu = menuId;
    }

    public boolean onNavigationClicked(View view) {
        if (getActivity() instanceof FragmentActivityBase) {
            ((FragmentActivityBase) getActivity()).pop();
            return true;
        }
        return false;
    }

    public final void push(Class<? extends FragmentBaseCompat> target, Map<String, Object> data) {
        if (getActivity() instanceof FragmentActivityBase) {
            ((FragmentActivityBase) getActivity()).push(target, data);
        }
    }

    public final void push(@AnimRes int enterAnimation, @AnimRes int exitAnimation, @NonNull Class<? extends FragmentBaseCompat> target, Map<String, Object> data) {
        if (getActivity() instanceof FragmentActivityBase) {
            ((FragmentActivityBase) getActivity()).push(enterAnimation, exitAnimation, target, data);
        }
    }

    public final void push(@AnimRes int enterAnimation, @AnimRes int exitAnimation, @NonNull Class<? extends FragmentBaseCompat> target) {
        if (getActivity() instanceof FragmentActivityBase) {
            ((FragmentActivityBase) getActivity()).push(enterAnimation, exitAnimation, target, null);
        }
    }

    public final void push(Class<? extends FragmentBaseCompat> target) {
        push(target, null);
    }

    public final void pushForResult(@AnimRes int enterAnimation, @AnimRes int exitAnimation, @NonNull Class<? extends FragmentBaseCompat> cls, Map<String, Object> data) {
        if (getActivity() instanceof FragmentActivityBase) {
            ((FragmentActivityBase) getActivity()).pushForResult(enterAnimation, exitAnimation, cls, data);
        }
    }

    public final void pop() {
        if (getActivity() instanceof FragmentActivityBase) {
            ((FragmentActivityBase) getActivity()).pop();
        }
    }

    public final void popToFragment(@NonNull Class<? extends FragmentBaseCompat> cls) {
        if (getActivity() instanceof FragmentActivityBase) {
            ((FragmentActivityBase) getActivity()).popToFragment(cls);
        }
    }

    public final void clearFragment(@NonNull Class<? extends FragmentBaseCompat> cls) {
        if (getActivity() instanceof FragmentActivityBase) {
            ((FragmentActivityBase) getActivity()).clearFragment(cls);
        }
    }

    public FragmentBaseCompat getLastNode() {
        return last;
    }

    public FragmentBaseCompat getNextNode() {
        return nextNode;
    }

    public void setLastNode(FragmentBaseCompat last) {
        this.last = last;
    }

    public void setNextNode(FragmentBaseCompat next) {
        nextNode = next;
    }

    /**
     * 请使用该回调方法刷新页面数据
     */
    public void onDataLoad() {
    }

    public void addDelegate(FragmentDelegate delegate) {
        if (null != delegate && !mDelegates.contains(delegate)) {
            mDelegates.add(delegate);
        }
    }

    public String obtainStringFromArgs(String key) {
        Bundle data = getArguments();
        if (null != data) {
            return data.getString(key);
        }
        return null;
    }

    public boolean obtainBooleanFromArgs(String key, boolean defaultValue) {
        Bundle data = getArguments();
        if (null != data) {
            return data.getBoolean(key);
        }
        return defaultValue;
    }

    public int obtainIntegerFromArgs(String key, int defaultValue) {
        Bundle data = getArguments();
        if (null != data) {
            return data.getInt(key);
        }
        return defaultValue;
    }

    public <T extends Parcelable> ArrayList<T> obtainParcelableArrayList(String key) {
        Bundle data = getArguments();
        if (null != data) {
            return data.getParcelableArrayList(key);
        }
        return null;
    }

    public void setCurrOper(int currOper) {
        mCurrOper = currOper;
    }

    public int getCurrOper() {
        return mCurrOper;
    }

    /**
     * 显示加载Dialog
     *
     * @return 当前的对话框对象
     */
    public void showLoadingDialog() {
        if (getActivity() instanceof ActivityBaseCompat) {
            ((ActivityBaseCompat) getActivity()).showLoadingDialog();
        }
    }

    /**
     * 关闭加载Dialog
     */
    public void dissmissLoadingDialog() {
        if (getActivity() instanceof ActivityBaseCompat) {
            ((ActivityBaseCompat) getActivity()).dissmissLoadingDialog();
        }
    }

    /**
     * 页面显示前调用该方法拉起等待页面
     */
    public void loadingStarted() {
        if (getActivity() instanceof ActivityBaseCompat) {
            ((ActivityBaseCompat) getActivity()).loadingStarted();
        }
    }

    /**
     * 页面加载完成调用该方法关闭等待页面
     */
    public void loadingFinished() {
        if (getActivity() instanceof ActivityBaseCompat) {
            ((ActivityBaseCompat) getActivity()).loadingFinished();
        }
    }

    public void setOnFragmentResultListener(OnFragmentResultListener onFragmentResultListener) {
        this.onFragmentResultListener = onFragmentResultListener;
    }

    public final void startFragmentForResult(int requestCode, Class<? extends FragmentBaseCompat> target, Map<String, Object> data) {
        if (null == data) data = new HashMap<>();
        data.put(KEY_REQUEST_CODE, requestCode);
        pushForResult(R.anim.open_right_in, R.anim.close_left_out, target, data);
    }

    public final void setResult(int resultCode, Bundle data) {
        mResultCode = resultCode;
        mResultData = data;
        mCalledSetResult = true;
    }

    public final void finish() {
        if (!mCalledSetResult) throw new RuntimeException("请先调用setResult方法");
        pop();
        FragmentManager manager = getActivity().getSupportFragmentManager();
        manager.beginTransaction().remove(this).commitAllowingStateLoss();
        // 隐藏软键盘
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        if (null != onFragmentResultListener) {
            onFragmentResultListener.onFragmentResult(obtainIntegerFromArgs(KEY_REQUEST_CODE, -1), mResultCode, mResultData);
        }
    }

//    public LayoutInflater getLayoutInflater() {
//        return LayoutInflater.from(getContext());
//    }

    protected boolean needUpdateToolbar() {
        return true;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (onNeedRefresh()) {
                onDataLoad();
                mCurrOper = OPER_BACK;
            }
            if (getActivity() instanceof ActivityBaseCompat && needUpdateToolbar()) {
                if (0 != mNavigationDrawable) {
                    ((ActivityBaseCompat) getActivity()).setNavigationDrawable(mNavigationDrawable);
                } else {
                    ((ActivityBaseCompat) getActivity()).setNavigationDrawable(R.drawable.return_btn);
                }
                if (!TextUtils.isEmpty(mTitle)) {
                    getActivity().setTitle(mTitle);
                } else {
                    getActivity().setTitle("");
                }
                if (0 != mTitleColor) {
                    getActivity().setTitleColor(mTitleColor);
                } else {
                    getActivity().setTitleColor(R.color.white);
                }
                if (0 != mToolbarBackgroundColor) {
                    ((ActivityBaseCompat) getActivity()).setToolbarBackgroundColor(mToolbarBackgroundColor);
                } else {
                    ((ActivityBaseCompat) getActivity()).setToolbarBackgroundColor(R.color.toolbar_background_color);
                }
                if (0 != mToolbarMenu) {
                    ((ActivityBaseCompat) getActivity()).inflateMenu(mToolbarMenu);
                } else {
                    ((ActivityBaseCompat) getActivity()).clearMenu();
                }
            }
        }
    }

    /**
     * 指定是否需要进行Data Load操作
     *
     * @return
     */
    protected boolean onNeedRefresh() {
        return OPER_PUSH == mCurrOper;
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
    }

    @Override
    public void onRequestSuccess(String url, ResObj result, boolean isFrmCache) {
        if (getActivity() instanceof ActivityBaseCompat) {
            ((ActivityBaseCompat) getActivity()).doRequestError(url, HttpStatus.SC_OK, result.getMsg());
        }
    }

    @Override
    public void onRequestError(String url, int statusCode, String error) {
        if (getActivity() instanceof ActivityBaseCompat) {
            ((ActivityBaseCompat) getActivity()).doRequestError(url, statusCode, error);
        }
    }

    protected void onMenuItemClick(MenuItem item) {
    }

    /**
     * 是否需要手动调用Activity #setOnMenuItemClickListener()方法
     *
     * @return
     */
    protected boolean needSetItemClick() {
        return false;
    }

    public void clearMenu() {
        if (getActivity() instanceof ActivityBaseCompat) {
            ((ActivityBaseCompat) getActivity()).clearMenu();
        }
    }
}