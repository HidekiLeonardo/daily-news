package com.hideki.daily_news.noticia.dto;

import java.time.OffsetDateTime;

public record NoticiaResponse(String titulo, OffsetDateTime data, String url, String descricao, String imagem, String fonte) {
}
