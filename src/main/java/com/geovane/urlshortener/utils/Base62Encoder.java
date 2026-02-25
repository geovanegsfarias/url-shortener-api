package com.geovane.urlshortener.utils;

public class Base62Encoder {
    private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static String encoder(long id) {
        StringBuilder shortCode = new StringBuilder();
        if (id == 0) {
            return "0";
        }
        while (id > 0) {
            char digit = CHARACTERS.charAt((int) (id%62));
            shortCode.append(digit);
            id = id/62;
        }
        shortCode.reverse();
        return shortCode.toString();
    }

}