package com.hideki.daily_news.feed;

import com.hideki.daily_news.noticia.dto.NoticiaResponse;

import java.util.List;

public record FeedResponse(String categoria, String resumo, List<NoticiaResponse> noticias) {
}
