package com.example.susong.testmvp.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.susong.testmvp.R;
import com.example.susong.testmvp.util.DensityUtil;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.Toolbar;

public class DefaultToolbar extends AbsToolbar {
    private OnNavigationClickListener onNavigationClickListener;
    private TextView mTitleView;
    private TextView mNavigationView;

    private OnClickListener titleClickListener;

    public DefaultToolbar(Context context) {
        super(context);
        initView(context);
    }

    public DefaultToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public DefaultToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        setContentInsetsAbsolute(0, 0);
        setBackgroundColor(getResources().getColor(R.color.toolbar_background_color));
        setTitleTextAppearance(getContext(), R.style.ToolbarTitleStyle);
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true);
        int[] actionBarSizeArr = new int[]{android.R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a = context.obtainStyledAttributes(typedValue.data, actionBarSizeArr);
        int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();
        setLayoutParams(new Toolbar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, actionBarSize));
        mNavigationView = new TextView(context);
        Toolbar.LayoutParams lp = new LayoutParams(DensityUtil.dip2px(60.0f), ViewGroup.LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
        mNavigationView.setLayoutParams(lp);
        mNavigationView.setClickable(true);
        mNavigationView.setPadding(DensityUtil.dip2px(10), 0, 0, 0);
        mNavigationView.setMinWidth(DensityUtil.dip2px(56));
        mNavigationView.setMaxWidth(DensityUtil.dip2px(100));
        Drawable returnImg = getContext().getResources().getDrawable(R.drawable.return_btn);
        if (null != returnImg) {
            returnImg.setBounds(0, 0, returnImg.getMinimumWidth(), returnImg.getMinimumHeight());
        }
        mNavigationView.setCompoundDrawables(returnImg, null, null, null);
        mNavigationView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onNavigationClickListener) {
                    onNavigationClickListener.onNavigationClick(v);
                }
            }
        });
        addView(mNavigationView);
        mTitleView = new TextView(context);
        Toolbar.LayoutParams lp1 = new Toolbar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp1.gravity = Gravity.CENTER;
        mTitleView.setLayoutParams(lp1);
        if (titleClickListener != null) {
            mTitleView.setOnClickListener(titleClickListener);
        }
        mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14.0f);
        mTitleView.setTextColor(Color.WHITE);
        addView(mTitleView);
    }

    @Override
    public void setOnNavigationClickListener(OnNavigationClickListener onNavigationClickListener) {
        this.onNavigationClickListener = onNavigationClickListener;
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitleView.setText(title);
    }

    @Override
    public void setTitle(@StringRes int resId) {
        mTitleView.setText(getResources().getString(resId));
    }

    @Override
    public void setTitleColor(@ColorInt int colorId) {
        mTitleView.setTextColor(colorId);
    }

    @Override
    public void setNavigationDrawable(@DrawableRes int drawableId) {
        Drawable returnImg = getContext().getResources().getDrawable(drawableId);
        if (null != returnImg) {
            returnImg.setBounds(0, 0, returnImg.getMinimumWidth(), returnImg.getMinimumHeight());
        }
        mNavigationView.setCompoundDrawables(returnImg, null, null, null );
    }

    @Override
    public void setTitleClickListener(OnClickListener listener) {
        titleClickListener = listener;
        mTitleView.setOnClickListener(titleClickListener);
    }
}
