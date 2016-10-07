package com.lennonwoo.rubber.utils;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.graphics.Palette;

import com.lennonwoo.rubber.R;

public class Utils {

    public static String durationToString(int duration) {
        int second = duration % 60;
        int minute = duration / 60;
        StringBuilder builder = new StringBuilder();
        builder.append(minute);
        builder.append(":");
        if (second / 10 == 0) {
            builder.append("0");
        }
        builder.append(second);
        return builder.toString();
    }

    public static int getColor(Context context, int id) {
        return ContextCompat.getColor(context, id);
    }

    public static void showMessageOKCancel(Context context, String message, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", cancelListener)
                .create()
                .show();
    }

    public static int getVibrantColor(Context context, Palette palette) {
        return palette.getVibrantColor(getColor(context, R.color.colorAccent));
    }

    public static int getLightMutedColor(Context context, Palette palette) {
        return palette.getLightMutedColor(Utils.getColor(context, R.color.white));
    }

    public static int getLightVibrantColor(Context context, Palette palette) {
        return palette.getLightVibrantColor(Utils.getColor(context, R.color.colorPrimary));
    }

    public static void colorChangeAnim(int oldColor, int newColor, ValueAnimator.AnimatorUpdateListener listener) {
        ValueAnimator anim = ValueAnimator.ofArgb(oldColor, newColor);
        anim.addUpdateListener(listener);
        anim.setDuration(1000);
        anim.start();
    }
}
