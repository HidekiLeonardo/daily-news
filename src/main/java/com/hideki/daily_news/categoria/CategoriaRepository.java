package com.hideki.daily_news.categoria;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<CategoriaModel, Long> {
    boolean existsByNomeIgnoreCase(String nome);
}

