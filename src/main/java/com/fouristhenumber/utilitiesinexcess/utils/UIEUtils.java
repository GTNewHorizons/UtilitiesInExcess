package com.fouristhenumber.utilitiesinexcess.utils;

import java.text.DecimalFormat;

public class UIEUtils {

    private static final DecimalFormat COMMA_FORMAT = new DecimalFormat("#,###");

    public static String formatNumber(int number) {
        return COMMA_FORMAT.format(number);
    }

    public static String formatNumber(long number) {
        return COMMA_FORMAT.format(number);
    }
}
