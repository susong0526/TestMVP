package com.example.susong.testmvp.widget;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;

public abstract class AbsToolbar extends Toolbar {

    public interface OnNavigationClickListener {
        void onNavigationClick(View view);
    }

    public AbsToolbar(Context context) {
        super(context);
    }

    public AbsToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AbsToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public abstract void setOnNavigationClickListener(OnNavigationClickListener onNavigationClickListener);

    public abstract void setTitle(CharSequence title);

    public abstract void setTitle(@StringRes int resId);

    public abstract void setTitleColor(@ColorInt int colorId);

    public abstract void setNavigationDrawable(@DrawableRes int drawableId);

    public void setTitleSuper(CharSequence title) {
        super.setTitle(title);
    }

    public void setTitleClickListener(OnClickListener listener) {
    }
}
