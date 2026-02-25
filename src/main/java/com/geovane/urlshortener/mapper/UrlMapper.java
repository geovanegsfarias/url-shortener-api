package com.geovane.urlshortener.mapper;

import com.geovane.urlshortener.dto.CreateUrlRequestDto;
import com.geovane.urlshortener.dto.UrlResponseDto;
import com.geovane.urlshortener.dto.UrlStatsResponseDto;
import com.geovane.urlshortener.model.UrlEntity;

public class UrlMapper {

    public static UrlResponseDto mapUrlEntityToUrlResponseDto(UrlEntity entity) {
        return new UrlResponseDto(
                entity.getId(),
                entity.getUrl(),
                entity.getShortCode(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public static UrlStatsResponseDto mapUrlEntityToUrlStatsResponseDto(UrlEntity entity) {
        return new UrlStatsResponseDto(
                entity.getId(),
                entity.getUrl(),
                entity.getShortCode(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getAccessCount()
        );
    }

    public static UrlEntity mapCreateUrlRequestDtoToUrlEntity(CreateUrlRequestDto request) {
        return new UrlEntity(
                request.url()
        );
    }
}