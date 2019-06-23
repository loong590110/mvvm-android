package com.mylive.live.arch.permission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Create by zailongshi on 2019/6/22
 */
public final class PermissionsRequester {

    private int requestCode;
    private Activity activity;
    private List<String> permissions;
    private RequestResultHandler handler;

    private PermissionsRequester(Activity activity, int requestCode) {
        this.requestCode = requestCode;
        this.activity = activity;
    }

    public PermissionsRequester addHandler(RequestResultHandler handler) {
        this.handler = handler;
        return this;
    }

    public PermissionsRequester request(String... permissions) {
        if (permissions != null && permissions.length > 0) {
            if (this.permissions == null) {
                this.permissions = new ArrayList<>();
            }
            this.permissions.removeAll(Arrays.asList(permissions));
            this.permissions.addAll(Arrays.asList(permissions));
        }
        if (this.permissions != null && this.permissions.size() > 0) {
            ActivityCompat.requestPermissions(activity,
                    this.permissions.toArray(new String[]{}),
                    requestCode);
        }
        return this;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == this.requestCode) {
            if (handler == null)
                return;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                            permissions[i])) {
                        if (handler.shouldShowRequestPermissionRationale(permissions[i])) {
                            return;
                        }
                    } else {
                        if (handler.onPermissionIsDeniedAndNeverAsks(permissions[i])) {
                            return;
                        }
                    }
                }
            }
            handler.onAllEssentialPermissionsAreGranted();
        }
    }

    public static PermissionsRequester create(Activity activity, int requestCode) {
        return new PermissionsRequester(activity, requestCode);
    }

    public static abstract class RequestResultHandler {

        protected abstract void onAllEssentialPermissionsAreGranted();

        @SuppressWarnings({"WeakerAccess", "unused"})
        protected boolean shouldShowRequestPermissionRationale(String permission) {
            return false;
        }

        @SuppressWarnings({"WeakerAccess", "unused"})
        protected boolean onPermissionIsDeniedAndNeverAsks(String permission) {
            return false;
        }
    }
}
