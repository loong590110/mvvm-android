package com.mylive.live.view.home;

import com.mylive.live.component.ScrollEvent;

/**
 * Create by zailongshi on 2019/7/8
 */
public final class HomeScrollEvent extends ScrollEvent {

    private static class HomeScrollEventHolder {
        private final static HomeScrollEvent INSTANCE = new HomeScrollEvent();
    }

    public static HomeScrollEvent getInstance() {
        return HomeScrollEventHolder.INSTANCE;
    }

    private HomeScrollEvent() {
    }
}
