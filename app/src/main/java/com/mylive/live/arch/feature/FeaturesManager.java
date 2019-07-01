package com.mylive.live.arch.feature;

import android.arch.lifecycle.LifecycleOwner;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Developer Zailong Shi on 2019-07-01.
 */
public final class FeaturesManager implements Iterable<Feature> {

    private LifecycleOwner lifecycleOwner;
    private Map<Class<? extends Feature>, Feature> featureMap;

    private FeaturesManager(LifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;
    }

    public static FeaturesManager of(FragmentActivity activity) {
        return new FeaturesManager(activity);
    }

    public static FeaturesManager of(Fragment fragment) {
        return new FeaturesManager(fragment);
    }

    public <T extends Feature> FeaturesManager put(Class<T> featureClass) {
        Objects.requireNonNull(featureClass);
        if (featureMap == null) {
            featureMap = new HashMap<>();
        }
        try {
            if (lifecycleOwner instanceof FeaturesActivity) {
                Constructor<T> constructor = featureClass.getConstructor(
                        FeaturesActivity.class);
                featureMap.put(featureClass, constructor.newInstance(
                        (FeaturesActivity) lifecycleOwner));
            } else if (lifecycleOwner instanceof FeaturesFragment) {
                Constructor<T> constructor = featureClass.getConstructor(
                        FeaturesFragment.class);
                featureMap.put(featureClass, constructor.newInstance(
                        (FeaturesFragment) lifecycleOwner));
            }
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (InstantiationException e) {
            throw new IllegalStateException(e);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
        return this;
    }

    public <T extends Feature> T get(Class<Feature> featureClass) {
        if (featureMap != null) {
            //noinspection unchecked
            return (T) featureMap.get(featureClass);
        }
        return null;
    }

    @Override
    public Iterator<Feature> iterator() {
        if (featureMap == null) {
            return new Iterator<Feature>() {
                @Override
                public boolean hasNext() {
                    return false;
                }

                @Override
                public Feature next() {
                    return null;
                }
            };
        }
        return featureMap.values().iterator();
    }
}
