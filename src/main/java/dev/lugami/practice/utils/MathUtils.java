package dev.lugami.practice.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MathUtils {

    public int roundNearest(double d) {
        return (int) Math.round(d);
    }

    public int roundDown(double d) {
        return (int) Math.floor(d);
    }

    public int roundUp(double d) {
        return (int) Math.ceil(d);
    }

    public double roundDecimal(double d) {
        return Math.round(d * 10.0) / 10.0;
    }

    public double roundHalf(double d) {
        return Math.round(d * 2.0) / 2.0;
    }
}
