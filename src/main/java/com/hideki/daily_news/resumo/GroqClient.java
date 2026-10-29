package com.hideki.daily_news.resumo;

import com.hideki.daily_news.resumo.dto.GroqMessage;
import com.hideki.daily_news.resumo.dto.GroqRequest;
import com.hideki.daily_news.resumo.dto.GroqResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class GroqClient {
    private final RestClient restClient;
    private final GroqProperties properties;

    public GroqClient(GroqProperties properties) {
        this.restClient = RestClient.create(properties.url());
        this.properties = properties;
    }

    public String resumir(String prompt) {
        GroqRequest request = new GroqRequest(
                properties.model(),
                List.of(new GroqMessage("user", prompt))
        );

        GroqResponse response = restClient.post()
                .header("Authorization", "Bearer " + properties.key())
                .body(request)
                .retrieve()
                .body(GroqResponse.class);

        return response.choices().get(0).message().content();
    }
}
