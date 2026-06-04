package com.github.geovanegsfarias.mapper;

import com.github.geovanegsfarias.dto.UrlPostRequest;
import com.github.geovanegsfarias.dto.UrlResponse;
import com.github.geovanegsfarias.dto.UrlStatsResponse;
import com.github.geovanegsfarias.model.Url;
import org.springframework.stereotype.Component;

@Component
public class UrlMapper {

    public UrlResponse toUrlResponse(Url url) {
        return new UrlResponse(
                url.getId(),
                url.getUrl(),
                url.getShortCode(),
                url.getCreatedAt(),
                url.getUpdatedAt()
        );
    }

    public UrlStatsResponse toUrlStatsResponse(Url url) {
        return new UrlStatsResponse(
                url.getId(),
                url.getUrl(),
                url.getShortCode(),
                url.getCreatedAt(),
                url.getUpdatedAt(),
                url.getAccessCount()
        );
    }

    public Url toUrl(UrlPostRequest request) {
        return Url.builder().url(request.url()).build();
    }
}