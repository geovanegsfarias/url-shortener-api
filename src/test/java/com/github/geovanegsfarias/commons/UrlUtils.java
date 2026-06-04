package com.github.geovanegsfarias.commons;

import com.github.geovanegsfarias.model.Url;
import com.github.geovanegsfarias.utils.Base62Encoder;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class UrlUtils {

    public Url newUrlToSave() {
        return Url.builder()
                .url("https://github.com/")
                .build();
    }

    public Url newSavedUrl() {
        return Url.builder()
                .id(1L)
                .url("https://github.com/")
                .shortCode(Base62Encoder.encoder(1L))
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .accessCount(0)
                .build();
    }

}
