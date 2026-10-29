package com.hideki.daily_news.noticia;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "api")
public record GnewsProperties(String key, int max, String url, String country, String language) {
}
