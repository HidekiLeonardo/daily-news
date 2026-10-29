package com.hideki.daily_news.resumo;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "robo")
public record GroqProperties (String key,String url, String model){
}
