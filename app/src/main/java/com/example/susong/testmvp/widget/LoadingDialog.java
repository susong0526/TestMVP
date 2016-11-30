package com.example.susong.testmvp.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.widget.TextView;

import com.example.susong.testmvp.R;

/**
 * 加载提示框
 */
public class LoadingDialog extends BaseDialog {

    private TextView tvLoading;
    private Context context;

    public LoadingDialog(Context context) {
        super(context);
        this.context = context;
        setContentView(R.layout.loading_dialog_layout);
        tvLoading = (TextView) findViewById(R.id.tvLoading);
        setCanceledOnTouchOutside(false);
    }

    public LoadingDialog(Context context, int theme) {
        super(context, theme);
    }

    protected LoadingDialog(Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public void setTitle(String title) {
        tvLoading.setText((!TextUtils.isEmpty(title)) ? title : context.getString(R.string.loading));
    }

    @Override
    public void dismiss() {
        if (this != null)
            super.dismiss();
    }

    @Override
    public void show() {
        if (this != null)
            super.show();

    }
}
