package com.mylive.live.imageloader;

import android.content.Context;
import android.widget.ImageView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by Developer Zailong Shi on 2020-02-11.
 */
public final class FrescoImageLoader implements Displayable {

    public FrescoImageLoader(Context context) {
        Fresco.initialize(context);
    }

    private void displayInternal(ImageView imageView, String uri, Optional optional) {
        if (imageView instanceof SimpleDraweeView) {
            ((SimpleDraweeView) imageView).setImageURI(uri);
        }
    }

    @Override
    public void display(ImageView imageView, String uri) {
        displayInternal(imageView, uri, null);
    }

    @Override
    public void display(ImageView imageView, String uri, Optional optional) {
        displayInternal(imageView, uri, optional);
    }
}
