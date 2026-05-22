package com.github.geovanegsfarias.mapper;

import com.github.geovanegsfarias.dto.CreateUrlRequestDto;
import com.github.geovanegsfarias.dto.UrlResponseDto;
import com.github.geovanegsfarias.dto.UrlStatsResponseDto;
import com.github.geovanegsfarias.model.UrlEntity;

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