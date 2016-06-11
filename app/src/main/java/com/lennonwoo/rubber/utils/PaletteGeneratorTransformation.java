package com.lennonwoo.rubber.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.graphics.Palette;
import android.widget.ImageView;

import com.squareup.picasso.Transformation;

import java.util.Map;
import java.util.WeakHashMap;

public class PaletteGeneratorTransformation implements Transformation {
    private static final Map<Bitmap, Palette> CACHE = new WeakHashMap<>();
    private final int    numColors;

    @Override public Bitmap transform (final Bitmap source) {
        if (!CACHE.containsKey (source)) {
            final Palette palette = numColors > 0
                    ? Palette.generate (source, numColors)
                    : Palette.generate (source);
            CACHE.put (source, palette);
        }

        return source;
    }

    @Override public String key () {
        return getClass ().getCanonicalName () + ":" + numColors;
    }

    public PaletteGeneratorTransformation () {
        this (0);
    }

    public PaletteGeneratorTransformation (final int c) {
        numColors = c;
    }

    public static abstract class Callback
            implements com.squareup.picasso.Callback {
        private final ImageView target;

        public Callback (final ImageView t) {
            target = t;
        }

        @Override public void onSuccess () {
            onPalette (CACHE.get (((BitmapDrawable) target.getDrawable ()).getBitmap ()));
        }

        @Override public void onError () {
            onPalette (null);
        }

        public abstract void onPalette (final Palette palette);
    }
}
