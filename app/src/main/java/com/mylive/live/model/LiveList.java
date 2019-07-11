package com.mylive.live.model;

import java.util.List;

public class LiveList {
    public int type;
    public List<LiveListItem> list;

    public class LiveListItem {
        public long id;
        public String desc;
    }
}
