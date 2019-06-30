package com.mylive.live.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.StringRes;

import com.mylive.live.R;

/**
 * Created by Developer Zailong Shi on 2019-06-28.
 */
public final class AlertDialog extends Dialog implements DialogInterface {

    private LayoutParams lparams;

    private AlertDialog(LayoutParams lparams) {
        this(lparams.context);
        this.lparams = lparams;
    }

    private AlertDialog(Context context) {
        super(context, R.style.Dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_alert);
        if (lparams.cancelable) {
            setCancelable(true);
        }
        if (lparams.canceledOnTouchOutside) {
            setCanceledOnTouchOutside(true);
        }
        if (lparams.onCancelListener != null) {
            setOnCancelListener(lparams.onCancelListener);
        }
        if (lparams.onDismissListener != null) {
            setOnDismissListener(lparams.onDismissListener);
        }
        if (lparams.onKeyListener != null) {
            setOnKeyListener(lparams.onKeyListener);
        }
        if (lparams.onShowListener != null) {
            setOnShowListener(lparams.onShowListener);
        }
        if (lparams.title != null) {
            //((TextView)findViewById(R.id.txt_title)).setText(lparams.title);
        }
    }

    public static class Builder {

        private LayoutParams lparams;
        private Resources res;

        public Builder(Context context) {
            lparams = new LayoutParams();
            lparams.context = context;
            res = context.getResources();
        }

        public Builder setTitle(String title) {
            lparams.title = title;
            return this;
        }

        public Builder setTitle(@StringRes int stringResId) {
            lparams.title = res.getString(stringResId);
            return this;
        }

        public Builder setMessage(String message) {
            lparams.message = message;
            return this;
        }

        public Builder setMessage(@StringRes int stringResId) {
            lparams.message = res.getString(stringResId);
            return this;
        }

        public Builder setConfirmText(String confirmText) {
            lparams.confirmText = confirmText;
            return this;
        }

        public Builder setConfirmText(@StringRes int stringResId) {
            lparams.confirmText = res.getString(stringResId);
            return this;
        }

        public Builder setCancelText(String cancelText) {
            lparams.cancelText = cancelText;
            return this;
        }

        public Builder setCancelText(@StringRes int stringResId) {
            lparams.cancelText = res.getString(stringResId);
            return this;
        }

        public Builder setConfirmClickListener(OnClickListener l) {
            lparams.onConfirmClickListener = l;
            return this;
        }

        public Builder setCancelClickListener(OnClickListener l) {
            lparams.onCancelClickListener = l;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            lparams.cancelable = cancelable;
            return this;
        }

        public Builder setCanceledOnTouchOutside(boolean cancelable) {
            lparams.canceledOnTouchOutside = cancelable;
            return this;
        }

        public Builder setOnCancelListener(OnCancelListener l) {
            lparams.onCancelListener = l;
            return this;
        }

        public Builder setOnDismissListener(OnDismissListener l) {
            lparams.onDismissListener = l;
            return this;
        }

        public Builder setOnKeyListener(OnKeyListener l) {
            lparams.onKeyListener = l;
            return this;
        }

        public Builder setOnShowListener(OnShowListener l) {
            lparams.onShowListener = l;
            return this;
        }

        public AlertDialog create() {
            return new AlertDialog(lparams);
        }

        public AlertDialog show() {
            AlertDialog dialog = create();
            dialog.show();
            return dialog;
        }
    }

    private static class LayoutParams {
        private Context context;
        private String title;
        private String message;
        private String confirmText;
        private String cancelText;
        private OnClickListener onConfirmClickListener;
        private OnClickListener onCancelClickListener;
        private boolean cancelable;
        private boolean canceledOnTouchOutside;
        private OnCancelListener onCancelListener;
        private OnDismissListener onDismissListener;
        private OnKeyListener onKeyListener;
        private OnShowListener onShowListener;
    }
}
