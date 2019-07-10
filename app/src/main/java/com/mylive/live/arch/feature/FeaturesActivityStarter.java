package com.mylive.live.arch.feature;

import com.mylive.live.arch.starter.ActivityStartProxy;
import com.mylive.live.arch.starter.ActivityStarter;
import com.mylive.live.arch.starter.Finisher;

import java.util.Objects;

/**
 * Created by Developer Zailong Shi on 2018/12/21.
 */
public class FeaturesActivityStarter<T extends FeaturesActivity>
        extends ActivityStarter<T>
        implements Starter {

    @Override
    public Finisher start(Feature feature) {
        Objects.requireNonNull(feature);
        if (feature.getContext() != null
                && !ActivityStartProxy.ifPrevent(feature.getLifecycleOwner())) {
            intent.setClass(feature.getContext(), getTargetActivity());
            if (feature.getLifecycleOwner() instanceof FeaturesActivity) {
                ActivityStartProxy.startActivity(
                        (FeaturesActivity) feature.getLifecycleOwner(),
                        intent);
            } else if (feature.getLifecycleOwner() instanceof FeaturesFragment) {
                ActivityStartProxy.startActivity(
                        (FeaturesFragment) feature.getLifecycleOwner(),
                        intent);
            }
        }
        return () -> {
            if (feature.getActivity() != null) {
                feature.getActivity().finish();
            }
        };
    }

    @Override
    public void startForResult(Feature feature, int requestCode) {
        Objects.requireNonNull(feature);
        if (feature.getContext() == null
                || ActivityStartProxy.ifPrevent(feature.getLifecycleOwner())) {
            return;
        }
        intent.setClass(feature.getContext(), getTargetActivity());
        if (feature.getLifecycleOwner() instanceof FeaturesActivity) {
            ActivityStartProxy.startActivityForResult(
                    (FeaturesActivity) feature.getLifecycleOwner(),
                    intent, requestCode);
        } else if (feature.getLifecycleOwner() instanceof FeaturesFragment) {
            ActivityStartProxy.startActivityForResult(
                    (FeaturesFragment) feature.getLifecycleOwner(),
                    intent, requestCode);
        }
    }
}
