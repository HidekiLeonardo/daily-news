package com.hideki.daily_news.noticia;

import com.hideki.daily_news.noticia.dto.GNewsResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.OffsetDateTime;

@Component
public class GnewsClient {
    private final RestClient restClient;
    private final GnewsProperties properties;

    public GnewsClient(GnewsProperties properties) {
        this.restClient = RestClient.create(properties.url());
        this.properties = properties;
    }

    public GNewsResponse getNoticiasPorCategoria(String categoria, OffsetDateTime de, OffsetDateTime ate) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("category", categoria)
                        .queryParam("lang", properties.language())
                        .queryParam("country", properties.country())
                        .queryParam("max", properties.max())
                        .queryParam("apikey", properties.key())
                        .queryParam("from", de.toInstant().toString())
                        .queryParam("to", ate.toInstant().toString())
                        .build())
                .retrieve()
                .body(GNewsResponse.class);
    }
}
