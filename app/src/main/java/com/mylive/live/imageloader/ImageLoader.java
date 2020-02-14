package com.mylive.live.imageloader;

import android.widget.ImageView;

/**
 * Created by Developer Zailong Shi on 2020-02-11.
 */
public final class ImageLoader implements Displayable {

    private static class Holder {
        private static ImageLoader INSTANCE = new ImageLoader();
    }

    public static ImageLoader getInstance() {
        return Holder.INSTANCE;
    }

    private Displayable displayable;

    private ImageLoader() {
    }

    private Displayable getDisplayable() {
        if (displayable == null) {
            throw new IllegalStateException("Please initialize image loader first!");
        }
        return displayable;
    }

    public void init(Displayable displayable) {
        this.displayable = displayable;
    }

    @Override
    public void display(ImageView imageView, String uri) {
        getDisplayable().display(imageView, uri);
    }
}
