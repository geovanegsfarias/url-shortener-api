package com.github.geovanegsfarias.commons;

import com.github.geovanegsfarias.model.Url;
import com.github.geovanegsfarias.utils.Base62Encoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

@Component
public class UrlUtils {

    public Url newUrlToSave() {
        return Url.builder()
                .url("https://github.com/")
                .build();
    }

    public Url newSavedUrl() {
        var instant = Instant.parse("2026-06-04T20:00:00Z");

        return Url.builder()
                .id(1L)
                .url("https://github.com/")
                .shortCode(Base62Encoder.encoder(1L))
                .createdAt(instant)
                .updatedAt(instant)
                .accessCount(0)
                .build();
    }

}
