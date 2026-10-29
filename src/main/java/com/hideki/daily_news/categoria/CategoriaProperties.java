package com.hideki.daily_news.categoria;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "api")
public record CategoriaProperties(List<String> categorias) {
}
