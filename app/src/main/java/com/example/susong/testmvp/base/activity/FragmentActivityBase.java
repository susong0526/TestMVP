package com.example.susong.testmvp.base.activity;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.AnimRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;

import com.jlhm.personal.R;
import com.jlhm.personal.fragment.FragmentBaseCompat;
import com.jlhm.personal.thirdparty.bugly.CrashReporterUtils;
import com.jlhm.personal.ui.FragmentBase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Scott Smith  @Date 2016年04月16/4/14日 09:25
 */
public abstract class FragmentActivityBase extends ActivityBaseCompat {
    // 当前正在显示的Fragment Tag
    private final String KEY_CURR_FR_TAG = "curr_fragment_tag";
    private String mCurrFragmentTag;
    // Fragment切入动画
    @AnimRes
    private int mEnterAnimation = R.anim.open_right_in;
    // Fragment退出动画
    @AnimRes
    private int mExitAnimation = R.anim.close_left_out;
    // Fragment POP进入动画
    @AnimRes
    private int mPopInAnimation = R.anim.open_left_in;
    // Fragment POP退出动画
    @AnimRes
    private int mPopOutAnimation = R.anim.close_right_out;
    private final FragmentManager mFragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != savedInstanceState) {
            mCurrFragmentTag = savedInstanceState.getString(KEY_CURR_FR_TAG);
            if (!TextUtils.isEmpty(mCurrFragmentTag)) {
                List<Fragment> fragments = getSupportFragmentManager().getFragments();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_NONE);
                if (null != fragments && fragments.size() > 0) {
                    for (Fragment fragment : fragments) {
                        if (null != fragment && !fragment.isDetached()) {
                            if (mCurrFragmentTag.equals(fragment.getTag())) {
                                transaction.show(fragment);
                            } else {
                                transaction.hide(fragment);
                            }
                        }
                    }
                }
                transaction.commitAllowingStateLoss();
            }
        }
    }

    @Override
    public void onNavigationClick(View view) {
        Fragment targetFr = getSupportFragmentManager().findFragmentByTag(mCurrFragmentTag);
        if (targetFr instanceof FragmentBaseCompat) {
            if (!((FragmentBaseCompat) targetFr).onNavigationClicked(view)) {
                super.onNavigationClick(view);
            }
        } else {
            super.onNavigationClick(view);
        }
    }


    @IdRes
    public abstract int containerId();

    public final void push(@NonNull Class<? extends FragmentBaseCompat> cls, Map<String, Object> data) {
        push(mEnterAnimation, mExitAnimation, cls, data);
    }

    public final void push(@AnimRes int enterAnimation, @AnimRes int exitAnimation, @NonNull Class<? extends FragmentBaseCompat> cls, Map<String, Object> data) {
        if (cls.getName().equals(mCurrFragmentTag)) return;
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.setCustomAnimations(enterAnimation, exitAnimation);
        FragmentBaseCompat currFr = null;
        if (!TextUtils.isEmpty(mCurrFragmentTag)) {
            currFr = (FragmentBaseCompat) mFragmentManager.findFragmentByTag(mCurrFragmentTag);
            if (null != currFr && currFr.isAdded()) {
                transaction.hide(currFr);
            }
        }
        FragmentBaseCompat targetFr = (FragmentBaseCompat) mFragmentManager.findFragmentByTag(cls.getName());
        if (null != targetFr && targetFr.isAdded()) {
            putData(targetFr.getArguments(), data);
            targetFr.setCurrOper(FragmentBaseCompat.OPER_PUSH);
            transaction.show(targetFr);
            targetFr.setLastNode(currFr);
            if(null != currFr) {
                currFr.setNextNode(targetFr);
            }
            mCurrFragmentTag = targetFr.getTag();
        }
        if (null == targetFr) {
            try {
                targetFr = cls.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                CrashReporterUtils.postCatchedException(e);
            }
            if (null != targetFr) {
                Bundle args = new Bundle();
                putData(args, data);
                targetFr.setArguments(args);
                targetFr.setCurrOper(FragmentBaseCompat.OPER_PUSH);
                transaction.add(containerId(), targetFr, cls.getName());
                targetFr.setLastNode(currFr);
                mCurrFragmentTag = targetFr.getTag();
            }
        }
        transaction.commitAllowingStateLoss();
    }

    public final void pushForResult(@AnimRes int enterAnimation, @AnimRes int exitAnimation, @NonNull Class<? extends FragmentBaseCompat> cls, Map<String, Object> data) {
        if (cls.getName().equals(mCurrFragmentTag)) return;
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.setCustomAnimations(enterAnimation, exitAnimation);
        FragmentBaseCompat currFr = null;
        if (!TextUtils.isEmpty(mCurrFragmentTag)) {
            currFr = (FragmentBaseCompat) mFragmentManager.findFragmentByTag(mCurrFragmentTag);
            if (null != currFr && currFr.isAdded()) {
                transaction.hide(currFr);
            }
        }
        FragmentBaseCompat targetFr = (FragmentBaseCompat) mFragmentManager.findFragmentByTag(cls.getName());
        if (null != targetFr && targetFr.isAdded()) {
            putData(targetFr.getArguments(), data);
            targetFr.setCurrOper(FragmentBaseCompat.OPER_PUSH);
            targetFr.setOnFragmentResultListener(currFr);
            transaction.show(targetFr);
            targetFr.setLastNode(currFr);
            currFr.setNextNode(targetFr);
            mCurrFragmentTag = targetFr.getTag();
        }
        if (null == targetFr) {
            try {
                targetFr = cls.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                CrashReporterUtils.postCatchedException(e);
            }
            if (null != targetFr) {
                Bundle args = new Bundle();
                putData(args, data);
                targetFr.setArguments(args);
                targetFr.setOnFragmentResultListener(currFr);
                targetFr.setCurrOper(FragmentBaseCompat.OPER_PUSH);
                transaction.add(containerId(), targetFr, cls.getName());
                targetFr.setLastNode(currFr);
                mCurrFragmentTag = targetFr.getTag();
            }
        }
        transaction.commitAllowingStateLoss();
    }

    protected final void push(@NonNull Class<? extends FragmentBaseCompat> cls) {
        push(cls, null);
    }

    public final void popToFragment(@NonNull Class<? extends FragmentBaseCompat> cls) {
        if(!TextUtils.isEmpty(mCurrFragmentTag)) {
            FragmentBaseCompat currFr = (FragmentBaseCompat) getSupportFragmentManager().findFragmentByTag(mCurrFragmentTag);

            if(null != currFr) {
                FragmentBaseCompat targetFr = null;
                while (null != (targetFr = currFr.getLastNode())) {
                    if(cls.getName().equals(targetFr.getTag())) {
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        List<Fragment> fragments = fragmentManager.getFragments();
                        if(null != fragments && fragments.size() > 0) {
                            for(Fragment fragment : fragments) {
                                transaction.hide(fragment);
                            }
                        }

                        transaction.show(targetFr);
                        transaction.commitAllowingStateLoss();

                        mCurrFragmentTag = targetFr.getTag();

                        break;
                    }
                }
            }
        }
    }

    public final void clearFragment(@NonNull Class<? extends FragmentBaseCompat> cls) {
        if(!TextUtils.isEmpty(mCurrFragmentTag)) {
            FragmentBaseCompat currFr = (FragmentBaseCompat) getSupportFragmentManager().findFragmentByTag(mCurrFragmentTag);

            if(null != currFr) {
                // clear 当前Fragment
                if (cls.getName().equals(mCurrFragmentTag)) {
                    FragmentBaseCompat lastNode = currFr.getLastNode();
                    FragmentBaseCompat nextNode = currFr.getNextNode();

                    if(null == lastNode) {
                        mCurrFragmentTag = null;
                    } else {
                        mCurrFragmentTag = lastNode.getTag();
                        lastNode.setNextNode(nextNode);
                    }
                } else {
                    FragmentBaseCompat targetFr = null;
                    while (null != (targetFr = currFr.getLastNode())) {
                        if (cls.getName().equals(targetFr.getTag())) {
                            FragmentBaseCompat lastNode = targetFr.getLastNode();
                            FragmentBaseCompat nextNode = targetFr.getNextNode();
                            if (null != lastNode) {
                                lastNode.setNextNode(nextNode);
                            }

                            if (null != nextNode) {
                                nextNode.setLastNode(lastNode);
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    private void putData(Bundle args, Map<String, Object> maps) {
        if (null == args) return;
        if (null != maps && maps.size() > 0) {
            for (String key : maps.keySet()) {
                if (null != key) {
                    if (TextUtils.isEmpty(key)) continue;
                    Object value = maps.get(key);
                    // 整形
                    if (value instanceof Integer) {
                        args.putInt(key, (Integer) value);
                    }
                    // 长整形
                    if (value instanceof Long) {
                        args.putLong(key, (Long) value);
                    }
                    // Float
                    if (value instanceof Float) {
                        args.putFloat(key, (Float) value);
                    }
                    // Double
                    if (value instanceof Double) {
                        args.putDouble(key, (Double) value);
                    }
                    // Boolean
                    if (value instanceof Boolean) {
                        args.putBoolean(key, (Boolean) value);
                    }
                    // String
                    if (value instanceof String) {
                        args.putString(key, (String) value);
                    }
                    // Serializable
                    if (value instanceof Serializable) {
                        args.putSerializable(key, (Serializable) value);
                    }
                    // List (must be implments parceable inter )
                    if(value instanceof ArrayList) {
                        args.putParcelableArrayList(key, (ArrayList<? extends Parcelable>) value);
                    }
                 }
            }
        } else {
            args.clear();
        }
    }

    public final void pop() {
        pop(mPopInAnimation, mPopOutAnimation);
    }

    public final void pop(@AnimRes int popInAnimation, @AnimRes int popOutAnimation) {
        if (!TextUtils.isEmpty(mCurrFragmentTag)) {
            FragmentBaseCompat currFragment = (FragmentBaseCompat) mFragmentManager.findFragmentByTag(mCurrFragmentTag);
            if (null != currFragment && !currFragment.isDetached()) {
                FragmentBaseCompat last = currFragment.getLastNode();
                if (null != last && !last.isDetached() && last.isAdded()) {
                    FragmentTransaction transaction = mFragmentManager.beginTransaction();
                    transaction.setCustomAnimations(popInAnimation, popOutAnimation);
                    transaction.hide(currFragment);
                    transaction.show(last);
                    transaction.commitAllowingStateLoss();
                    currFragment.setLastNode(null);
                    mCurrFragmentTag = last.getTag();
                } else {
                    finish();
                }
            } else {
                finish();
            }
        }
    }

    public void setTransactionAnimation(@AnimRes int enterAnim, @AnimRes int exitAnim, @AnimRes int popInAnim, @AnimRes int popOutAnim) {
        mEnterAnimation = enterAnim;
        mExitAnimation = exitAnim;
        mPopInAnimation = popInAnim;
        mPopOutAnimation = popOutAnim;
    }

    /**
     * 对于平级页面,使用该方法进行跳转,例如Tab页面
     *
     * @param target 跳转的目标Fragment
     * @param enterAnim 进入动画
     * @param exitAnim 退出动画
     */
    public void switchTo(@NonNull Class<? extends FragmentBaseCompat> target, @AnimRes int enterAnim, @AnimRes int exitAnim) {
        if (target.getName().equals(mCurrFragmentTag)) return;
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.setCustomAnimations(enterAnim, exitAnim);
        FragmentBaseCompat currFr;
        if (!TextUtils.isEmpty(mCurrFragmentTag)) {
            currFr = (FragmentBaseCompat) mFragmentManager.findFragmentByTag(mCurrFragmentTag);
            if (null != currFr) transaction.hide(currFr);
        }
        FragmentBaseCompat targetFr = (FragmentBaseCompat) mFragmentManager.findFragmentByTag(target.getName());
        if (null != targetFr) {
            transaction.show(targetFr);
            mCurrFragmentTag = target.getName();
        } else {
            try {
                targetFr = target.newInstance();
                transaction.add(containerId(), targetFr, target.getName());
                mCurrFragmentTag = target.getName();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        transaction.commitAllowingStateLoss();
    }

    /**
     * 对于平级页面,使用该方法进行跳转,例如Tab页面
     *
     * @param target 跳转的目标Fragment
     * @param enterAnim 进入动画
     * @param exitAnim 退出动画
     */
    public void switchTo(@NonNull Class<? extends FragmentBase> target, FragmentBase fragmentBaseCompat, @AnimRes int enterAnim, @AnimRes int exitAnim) {
        if (target.getName().equals(mCurrFragmentTag)) return;
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.setCustomAnimations(enterAnim, exitAnim);
        FragmentBaseCompat currFr;
        if (!TextUtils.isEmpty(mCurrFragmentTag)) {
            currFr = (FragmentBaseCompat) mFragmentManager.findFragmentByTag(mCurrFragmentTag);
            if (null != currFr) transaction.hide(currFr);
        }
        FragmentBase targetFr = fragmentBaseCompat;
        if(fragmentBaseCompat==null){
            targetFr = (FragmentBase) mFragmentManager.findFragmentByTag(target.getName());
        }
        if (null != targetFr) {
            transaction.show(targetFr);
            mCurrFragmentTag = target.getName();
        } else {
            try {
                targetFr = target.newInstance();
                transaction.add(containerId(), targetFr, target.getName());
                mCurrFragmentTag = target.getName();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        transaction.commitAllowingStateLoss();
    }

    public void switchTo(@NonNull Class<? extends FragmentBaseCompat> target) {
        switchTo(target, 0, 0);
    }

    public void switchTo(@NonNull Class<? extends FragmentBase> target, FragmentBase fragmentBaseCompat) {
        switchTo(target, fragmentBaseCompat, 0, 0);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!TextUtils.isEmpty(mCurrFragmentTag)) {
            outState.putString(KEY_CURR_FR_TAG, mCurrFragmentTag);
        }

    }

    @Override
    public void onBackPressed() {
        if(null != onBackPressedListener && onBackPressedListener.onBackPressed()) return;
        pop();
        loadingFinished();
        dissmissLoadingDialog();
    }

    public void callSuperBackPressed() {
        super.onBackPressed();
    }
}
