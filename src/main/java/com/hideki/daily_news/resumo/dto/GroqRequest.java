package com.hideki.daily_news.resumo.dto;

import java.util.List;

public record GroqRequest (String model, List<GroqMessage> messages){
}
