package com.hideki.daily_news.noticia.dto;

import java.time.OffsetDateTime;

public record GNewsArticle(String title, String description, String url, String image, OffsetDateTime publishedAt, GNewsSource source) {
}
