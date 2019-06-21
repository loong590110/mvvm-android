package com.mylive.live.core.starter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by Developer Zailong Shi on 2018/12/21.
 */
public class ActivityStarter<T> implements Starter {

    private Class<T> targetActivity;
    protected Bundle bundle;

    protected ActivityStarter() {
        bundle = new Bundle();
    }

    @Override
    public Finisher start(FragmentActivity context) {
        if (ActivityStartProxy.ifPrevent(context))
            return () -> {
                if (context != null) {
                    context.finish();
                }
            };
        Intent intent = new Intent(context, getTargetActivity());
        intent.putExtras(bundle);
        ActivityStartProxy.startActivity(context, intent);
        return context::finish;
    }

    @Override
    public void startForResult(FragmentActivity context, int requestCode) {
        if (ActivityStartProxy.ifPrevent(context))
            return;
        Intent intent = new Intent(context, getTargetActivity());
        intent.putExtras(bundle);
        ActivityStartProxy.startActivityForResult(context, intent, requestCode);
    }

    private Class<T> getTargetActivity() {
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
