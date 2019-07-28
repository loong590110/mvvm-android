package com.mylive.live.arch.theme;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created By zailongshi on 2019/1/6.
 */
public final class StatusBarCompat {

    private StatusBarCompat() {
    }

    public static Settings getSettings(Activity activity) {
        return new Settings(activity);
    }

    public static class Settings {

        private Activity activity;
        private boolean lightMode;
        private boolean immersive;
        private int statusBarColor;

        Settings(Activity activity) {
            this.activity = activity;
            int systemUiVisibility = activity.getWindow().getDecorView().getSystemUiVisibility();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                lightMode = (systemUiVisibility & View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                        == View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                immersive = (systemUiVisibility & View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
                        == View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                statusBarColor = activity.getWindow().getStatusBarColor();
            }
        }

        public void apply() {
            int systemUiVisibility = activity.getWindow().getDecorView().getSystemUiVisibility();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!setStatusBarLightModeOnMIUI(activity, lightMode)
                        && !setStatusBarLightModeOnFlymeOS(activity, lightMode)) {
                    if (lightMode) {
                        systemUiVisibility |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                    } else {
                        systemUiVisibility &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                    }
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                if (immersive) {
                    systemUiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                } else {
                    systemUiVisibility &= ~View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                }
            }
            activity.getWindow().getDecorView().setSystemUiVisibility(systemUiVisibility);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.getWindow().setStatusBarColor(statusBarColor);
            }
        }

        public Settings setLightMode(boolean lightMode) {
            this.lightMode = lightMode;
            return this;
        }

        public Settings setImmersive(boolean immersive) {
            this.immersive = immersive;
            return this;
        }

        public Settings setStatusBarColor(int statusBarColor) {
            this.statusBarColor = statusBarColor;
            return this;
        }
    }

    /**
     * MIUI的沉浸支持透明白色字体和透明黑色字体
     * https://dev.mi.com/console/doc/detail?pId=1159
     */
    private static boolean setStatusBarLightModeOnMIUI(Activity activity, boolean lightMode) {
        try {
            @SuppressLint("PrivateApi")
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");

            Window window = activity.getWindow();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
            }

            Class<? extends Window> clazz = activity.getWindow().getClass();
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            int darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(activity.getWindow(), lightMode ? darkModeFlag : 0, darkModeFlag);
            return true;
        } catch (Exception ignore) {
        }
        return false;
    }

    /**
     * 设置状态栏图标为深色和魅族特定的文字风格，Flyme4.0以上
     */
    @SuppressWarnings("JavaReflectionMemberAccess")
    private static boolean setStatusBarLightModeOnFlymeOS(Activity activity, boolean lightMode) {
        try {
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            Field darkFlag = WindowManager.LayoutParams.class
                    .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
            Field meizuFlags = WindowManager.LayoutParams.class
                    .getDeclaredField("meizuFlags");
            darkFlag.setAccessible(true);
            meizuFlags.setAccessible(true);
            int bit = darkFlag.getInt(null);
            int value = meizuFlags.getInt(lp);
            if (lightMode) {
                value |= bit;
            } else {
                value &= ~bit;
            }
            meizuFlags.setInt(lp, value);
            activity.getWindow().setAttributes(lp);
            return true;
        } catch (Exception ignore) {
        }
        return false;
    }
}
