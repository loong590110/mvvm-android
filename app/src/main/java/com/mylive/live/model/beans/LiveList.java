package com.mylive.live.model.beans;

import java.util.List;

public class LiveList {
    public int type;
    public List<LiveListItem> list;

    public class LiveListItem {
        public long id;
        public String cover;
        public String desc;
        //local field
        public float dimenRatio;
    }
}
