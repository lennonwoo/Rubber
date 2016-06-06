package com.lennonwoo.rubber.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

public class PermissionChecker {

    public static final int STORAGE_REQUEST_CODE = 3;
    private OnPermissionResponse response;
    private Context context;
    private View baseView;

    public PermissionChecker(Context context, View baseView) {
        this.context = context;
        this.baseView = baseView;
    }

    public void check(final String permission, final OnPermissionResponse response) {
        this.response = response;
        if (ContextCompat.checkSelfPermission(context, permission) ==
                PackageManager.PERMISSION_GRANTED) {
            response.onAccepted();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                    permission)) {
                //TODO add dialog to explain why need permission
            } else {
                ActivityCompat.requestPermissions(((Activity) context),
                        new String[]{permission},
                        STORAGE_REQUEST_CODE);
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, final
    @NonNull int[] grantResults) {
        if (requestCode == PermissionChecker.STORAGE_REQUEST_CODE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                response.onAccepted();
            } else {
                response.onDecline();
            }
        }
    }

    public interface OnPermissionResponse {
        void onAccepted();

        void onDecline();
    }

}
