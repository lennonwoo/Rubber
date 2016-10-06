package com.lennonwoo.rubber.utils;

import android.animation.TimeInterpolator;

// This class is based on second-order system's underdamping state
public class StepResponseInterpolator implements TimeInterpolator {

    private static double dampingRatio;
    private static double phi;
    private static double sinPhi;
    private static double Wn;

    static {
        double lnOfOvershot =  Math.log(0.4);
        double sqrtPI =  Math.PI * Math.PI;
        double T = 4 * Math.PI; // T(period) means the oscillating times

        dampingRatio = Math.sqrt(lnOfOvershot * lnOfOvershot / (lnOfOvershot * lnOfOvershot + sqrtPI));
        phi = Math.acos(dampingRatio);
        sinPhi = Math.sqrt(1 - dampingRatio * dampingRatio);
        //  calculate oscillating frequency to ensure when input equals 1 the return value will be 1 too
        Wn = (T - phi) / sinPhi;
    }

    @Override
    public float getInterpolation(float input) {
        return (float) (
                1 - Math.pow(Math.E, -dampingRatio * Wn * input)
                        * Math.sin(Wn * sinPhi * input + phi)
                        / sinPhi
        );
    }
}
