package com.hideki.daily_news.resumo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface ResumoRepository extends JpaRepository<ResumoModel, Long> {
    Optional<ResumoModel> findByCategoriaNomeAndData(String nome, LocalDate data);
    boolean existsByCategoriaNomeAndData(String nome, LocalDate data);
}
