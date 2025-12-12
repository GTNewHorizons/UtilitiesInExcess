package com.fouristhenumber.utilitiesinexcess.utils;

public class ColorUtils {

    public static int getHexColorFromWoolMeta(int meta) {
        return switch (meta) {
            case 0 -> 0xebebeb; // White
            case 1 -> 0xf17716; // Orange
            case 2 -> 0xbe46b5; // Magenta
            case 3 -> 0x3cb0da; // Light Blue
            case 4 -> 0xf9c629; // Yellow
            case 5 -> 0x71ba1a; // Lime
            case 6 -> 0xee90ad; // Pink
            case 7 -> 0x3f4548; // Gray
            case 8 -> 0x8e8f87; // Light Gray
            case 9 -> 0x158a91; // Cyan
            case 10 -> 0x7b2bad; // Purple
            case 11 -> 0x353a9e; // Blue
            case 12 -> 0x734829; // Brown
            case 13 -> 0x556e1c; // Green
            case 14 -> 0xa12823; // Red
            case 15 -> 0x16161b; // Black
            default -> 0xebebeb;
        };
    }
}
