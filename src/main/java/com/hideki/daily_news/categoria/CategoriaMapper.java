package com.hideki.daily_news.categoria;

import com.hideki.daily_news.categoria.dto.CategoriaResponse;
import org.springframework.stereotype.Component;

@Component
public class CategoriaMapper {
    public CategoriaResponse toDTO (CategoriaModel categoriaModel) {
        return new CategoriaResponse(categoriaModel.getNome());
    }
}
