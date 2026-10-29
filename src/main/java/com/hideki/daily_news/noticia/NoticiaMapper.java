package com.hideki.daily_news.noticia;

import com.hideki.daily_news.categoria.CategoriaModel;
import com.hideki.daily_news.noticia.dto.GNewsArticle;
import com.hideki.daily_news.noticia.dto.NoticiaResponse;
import org.springframework.stereotype.Component;

@Component
public class NoticiaMapper {
    public NoticiaModel toModel(GNewsArticle article, CategoriaModel categoria){
        NoticiaModel model = new NoticiaModel();
        model.setTitulo(article.title());
        model.setDescricao(article.description());
        model.setUrl(article.url());
        model.setImagem(article.image());
        model.setData(article.publishedAt());
        model.setFonte(article.source().name());
        model.setCategoria(categoria);
        return model;
    }

    public NoticiaResponse toResponse(NoticiaModel noticia){
        NoticiaResponse response = new NoticiaResponse(
                noticia.getTitulo(),
                noticia.getData(),
                noticia.getUrl(),
                noticia.getDescricao(),
                noticia.getImagem(),
                noticia.getFonte()
        );
        return response;
    }
}
