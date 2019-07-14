package com.mylive.live.arch.starter;

import android.content.Intent;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * Created by Developer Zailong Shi on 2018/12/21.
 */
public class ActivityStarter<T extends FragmentActivity> implements Starter {

    private Class<T> targetActivity;
    @SuppressWarnings("WeakerAccess")
    protected Intent intent = new Intent();

    @Override
    public Finisher start(FragmentActivity context) {
        Objects.requireNonNull(context);
        if (!ActivityStartProxy.ifPrevent(context)) {
            intent.setClass(context, getTargetActivity());
            ActivityStartProxy.startActivity(context, intent);
        }
        return context::finish;
    }

    @Override
    public Finisher start(Fragment fragment) {
        Objects.requireNonNull(fragment);
        if (fragment.getContext() != null
                && !ActivityStartProxy.ifPrevent(fragment)) {
            intent.setClass(fragment.getContext(), getTargetActivity());
            ActivityStartProxy.startActivity(fragment, intent);
        }
        return () -> {
            if (fragment.getActivity() != null) {
                fragment.getActivity().finish();
            }
        };
    }

    @Override
    public void startForResult(FragmentActivity context, int requestCode) {
        if (ActivityStartProxy.ifPrevent(context)) {
            return;
        }
        intent.setClass(context, getTargetActivity());
        ActivityStartProxy.startActivityForResult(context, intent, requestCode);
    }

    @Override
    public void startForResult(Fragment fragment, int requestCode) {
        Objects.requireNonNull(fragment);
        if (fragment.getContext() == null
                || ActivityStartProxy.ifPrevent(fragment)) {
            return;
        }
        intent.setClass(fragment.getContext(), getTargetActivity());
        ActivityStartProxy.startActivityForResult(fragment, intent, requestCode);
    }

    protected Class<T> getTargetActivity() {
        if (targetActivity == null) {
            Type type = getClass().getGenericSuperclass();
            if (type instanceof ParameterizedType) {
                //noinspection unchecked
                targetActivity = (Class<T>) ((ParameterizedType) type)
                        .getActualTypeArguments()[0];
            }
        }
        return targetActivity;
    }
}
