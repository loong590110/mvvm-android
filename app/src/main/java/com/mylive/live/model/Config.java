package com.mylive.live.model;

/**
 * Created by Developer Zailong Shi on 2019-06-20.
 */
public class Config {
    private static Config instance;

    public static Config instance() {
        if (instance == null) {
            new Config(true);
        }
        return instance;
    }

    public Config() {
        this(false);
    }

    private Config(boolean empty) {
        if (empty && instance != null) {
            return;
        }
        instance = this;
    }

    public String appName;
    public String version;
    public String host;
    public String url;
    public String homePage;
}
