package com.mylive.live.imageloader;

import android.widget.ImageView;

/**
 * Created by Developer Zailong Shi on 2020-02-11.
 */
public interface Displayable {
    void display(ImageView imageView, String uri);

    void display(ImageView imageView, String uri, Optional optional);
}
