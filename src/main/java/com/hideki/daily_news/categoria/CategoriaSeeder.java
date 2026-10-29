package com.hideki.daily_news.categoria;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CategoriaSeeder implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaProperties categoriaProperties;

    public CategoriaSeeder(CategoriaRepository categoriaRepository, CategoriaProperties categoriaProperties) {
        this.categoriaRepository = categoriaRepository;
        this.categoriaProperties = categoriaProperties;
    }

    @Override
    public void run(String... args) throws Exception {
        for (String categoria : categoriaProperties.categorias()) {
            if (!categoriaRepository.existsByNomeIgnoreCase(categoria)) {
                CategoriaModel categoriaModel = new CategoriaModel(null, categoria);
                categoriaRepository.save(categoriaModel);
            }
        }
    }

}
