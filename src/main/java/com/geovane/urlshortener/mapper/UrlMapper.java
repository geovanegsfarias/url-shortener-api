package com.geovane.urlshortener.mapper;

import com.geovane.urlshortener.dto.UrlRequestDto;
import com.geovane.urlshortener.dto.UrlResponseDto;
import com.geovane.urlshortener.dto.UrlStatsDto;
import com.geovane.urlshortener.model.Url;

public class UrlMapper {

    public static UrlResponseDto toDto(Url entity) {
        return new UrlResponseDto(
                entity.getId(),
                entity.getOriginalUrl(),
                entity.getShortCode(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public static UrlStatsDto toStatusDto(Url entity) {
        return new UrlStatsDto(
                entity.getId(),
                entity.getOriginalUrl(),
                entity.getShortCode(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getAccessCount()
        );
    }

    public static Url toEntity(UrlRequestDto dto) {
        return new Url(
                dto.getOriginalUrl()
        );
    }
}
