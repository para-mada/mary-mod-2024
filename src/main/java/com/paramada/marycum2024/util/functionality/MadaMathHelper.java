package com.paramada.marycum2024.util.functionality;


public class MadaMathHelper {

    public static String humanizeDouble(float value) {
        float reducedValue = value / 10f;
        if (reducedValue >= 1_000_000_000) return String.format("%.1fB", reducedValue / 1_000_000_000);
        if (reducedValue >= 1_000_000)     return String.format("%.1fM", reducedValue / 1_000_000);
        if (reducedValue >= 1_000)         return String.format("%.1fk", reducedValue / 1_000);
        return String.format("%.1f", reducedValue);
    }
}
