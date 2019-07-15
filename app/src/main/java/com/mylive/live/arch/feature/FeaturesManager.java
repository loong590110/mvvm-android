package com.mylive.live.arch.feature;

import androidx.lifecycle.LifecycleOwner;
import android.os.Bundle;

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

    private FeaturesManagerOwner owner;
    private Map<Class<? extends Feature>, Feature> featureMap;

    private FeaturesManager(FeaturesManagerOwner owner) {
        this.owner = owner;
    }

    public static FeaturesManager of(FeaturesActivity activity) {
        return new FeaturesManager(activity);
    }

    public static FeaturesManager of(FeaturesFragment fragment) {
        return new FeaturesManager(fragment);
    }

    public <T extends Feature> FeaturesManager add(Class<T> featureClass) {
        return add(featureClass, null);
    }

    public <T extends Feature> FeaturesManager add(Class<T> featureClass, Bundle arguments) {
        Objects.requireNonNull(featureClass);
        if (featureMap == null) {
            featureMap = new HashMap<>();
        }
        try {
            Constructor<T> constructor = featureClass.getConstructor(
                    FeaturesManagerOwner.class);
            Feature feature = constructor.newInstance(owner);
            feature.setArguments(arguments);
            featureMap.put(featureClass, feature);
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

    public <T extends Feature> T find(Class<Feature> featureClass) {
        if (featureMap != null) {
            //noinspection unchecked
            return (T) featureMap.get(featureClass);
        }
        return null;
    }

    @Override
    public Iterator<Feature> iterator() {
        return new Iterator<Feature>() {
            int index = 0;
            Feature[] features;

            {
                if (featureMap != null && featureMap.size() > 0) {
                    features = featureMap.values().toArray(new Feature[]{});
                }
            }

            @Override
            public boolean hasNext() {
                return features != null && features.length > index;
            }

            @Override
            public Feature next() {
                return hasNext() ? features[index++] : null;
            }
        };
    }
}
