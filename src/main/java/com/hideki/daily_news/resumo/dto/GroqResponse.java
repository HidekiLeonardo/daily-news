package com.hideki.daily_news.resumo.dto;

import java.util.List;

public record GroqResponse (List<GroqChoice> choices){
}
