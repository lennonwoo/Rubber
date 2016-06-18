package com.lennonwoo.rubber.utils;

import android.animation.TimeInterpolator;

public class StepResponseInterpolator implements TimeInterpolator {

    @Override
    public float getInterpolation(float input) {
        double temp1 =  Math.log(0.3); //   this 0.5 means overshoot 50%
        double temp2 =  Math.PI * Math.PI;
        //  calculate damping ratio
        double d = Math.sqrt(temp1 * temp1 / (temp1 * temp1 + temp2));

        double temp3 = Math.sqrt(1 - d * d);
        double temp4 = Math.acos(d); // arc cosine of d
        double T = 5 * Math.PI;
        //  calculate oscillating frequency so when input equals 1 that the return value will be 1
        double Wn = (T - temp4) / temp3;
        return (float) (
                1 - Math.pow(Math.E, - d * Wn * input)
                        * Math.sin(Wn * temp3 * input + temp4)
                        / temp3
        );
    }
}
