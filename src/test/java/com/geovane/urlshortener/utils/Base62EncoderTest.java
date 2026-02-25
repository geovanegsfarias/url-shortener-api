package com.geovane.urlshortener.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Base62EncoderTest {

    @Test
    @DisplayName("Should return shortCode '0'")
    void encoderCase1() {
        String shortCode = Base62Encoder.encoder(0);
        assertEquals("0", shortCode);
    }

    @Test
    @DisplayName("Should return shortCode 'WdE'")
    void encoderCase2() {
        String shortCode = Base62Encoder.encoder(125440);
        assertEquals("WdE", shortCode);
    }
}