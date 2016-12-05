package com.example.susong.testmvp.widget;

import android.app.Dialog;
import android.content.Context;

import com.example.susong.testmvp.R;

public class BaseDialog extends Dialog {
    public BaseDialog(Context context) {
        super(context, R.style.CustomDialog);
    }

    public BaseDialog(Context context, int theme) {
        super(context, theme);
    }

    protected BaseDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
}
