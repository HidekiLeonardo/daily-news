package com.hideki.daily_news.calendario;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Component
public class Calendario {
    private static final ZoneId ZONA = ZoneId.of("America/Sao_Paulo");

    public LocalDate ontem(){
        return LocalDate.now(ZONA).minusDays(1);
    }
    public OffsetDateTime inicio(LocalDate data){
        return data.atStartOfDay(ZONA).toOffsetDateTime();
    }
    public OffsetDateTime fim(LocalDate data){
        return data.plusDays(1).atStartOfDay(ZONA).toOffsetDateTime();
    }
}
