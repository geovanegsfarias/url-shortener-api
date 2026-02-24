package com.geovane.urlshortener.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "urls")
public class UrlEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String url;
    @Column(unique = true)
    private String shortCode;
    @Column(updatable = false)
    @CreationTimestamp
    private Instant createdAt;
    @UpdateTimestamp
    private Instant updatedAt;
    private long accessCount;

    public UrlEntity() {
    }

    public UrlEntity(String url) {
        this.url = url;
    }

    public UrlEntity(Long id, String url, String shortCode) {
        this.id = id;
        this.url = url;
        this.shortCode = shortCode;
    }

    public UrlEntity(Long id, String url, String shortCode, long accessCount) {
        this.id = id;
        this.url = url;
        this.shortCode = shortCode;
        this.accessCount = accessCount;
    }

    public UrlEntity(String url, String shortCode, Instant createdAt, long accessCount) {
        this.url = url;
        this.shortCode = shortCode;
        this.createdAt = createdAt;
        this.accessCount = accessCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public long getAccessCount() {
        return accessCount;
    }

    public void setAccessCount(long accessCount) {
        this.accessCount = accessCount;
    }

}