package com.hideki.daily_news.noticia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public interface NoticiaRepository extends JpaRepository<NoticiaModel, Long> {
    boolean existsByUrl(String url);
    List<NoticiaModel> findByCategoriaNomeAndData(String nome, LocalDate data);
    List<NoticiaModel> findByCategoriaNomeAndDataBetween(String nome, OffsetDateTime inicio, OffsetDateTime fim);
}
