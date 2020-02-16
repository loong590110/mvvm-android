package com.mylive.live.imageloader;

/**
 * Created by Developer Zailong Shi on 2020-02-11.
 */
public final class ImageLoader {

    public static final class Config {
        private Displayable imageLoader;

        public Config(Displayable imageLoader) {
            this.imageLoader = imageLoader;
        }

        public Config set(Config config) {
            imageLoader = config.imageLoader;
            return this;
        }
    }

    private static class Holder {
        private final static Config config = new Config(null);
    }

    public static void init(Config config) {
        Holder.config.set(config);
    }

    public static Displayable getInstance() {
        if (Holder.config.imageLoader == null) {
            throw new IllegalStateException("Please initialize image loader first!");
        }
        return Holder.config.imageLoader;
    }

    private ImageLoader() {
    }
}
