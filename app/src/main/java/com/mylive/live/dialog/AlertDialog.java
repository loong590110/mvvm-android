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

        public AlertDialog create() {
            AlertDialog dialog = new AlertDialog(lparams);
            dialog.setCancelable(lparams.cancelable);
            return dialog;
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
    }
}
