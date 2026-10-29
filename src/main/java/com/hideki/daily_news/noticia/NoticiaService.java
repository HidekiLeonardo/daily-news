package com.hideki.daily_news.noticia;

import com.hideki.daily_news.calendario.Calendario;
import com.hideki.daily_news.categoria.CategoriaModel;
import com.hideki.daily_news.noticia.dto.GNewsArticle;
import com.hideki.daily_news.noticia.dto.GNewsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import java.util.List;

@Service
public class NoticiaService {
    private final NoticiaRepository noticiaRepository;
    private final GnewsClient gnewsClient;
    private final NoticiaMapper mapper;
    private final Calendario calendario;
    private static final Logger log = LoggerFactory.getLogger(NoticiaService.class);

    public NoticiaService(NoticiaRepository noticiaRepository, GnewsClient gnewsClient, NoticiaMapper mapper, Calendario calendario) {
        this.noticiaRepository = noticiaRepository;
        this.gnewsClient = gnewsClient;
        this.mapper = mapper;
        this.calendario = calendario;
    }

    public void buscarESalvar(CategoriaModel categoria, LocalDate dia) {
        GNewsResponse response = gnewsClient.getNoticiasPorCategoria(categoria.getNome(), calendario.inicio(dia), calendario.fim(dia));
        log.info("Categoria {}: {} artigos recebidos", categoria.getNome(), response.articles().size());
        for (GNewsArticle article : response.articles()) {
            if (!noticiaRepository.existsByUrl(article.url())) {
                NoticiaModel noticia = mapper.toModel(article, categoria);
                noticiaRepository.save(noticia);
                log.info("Salvando: {}", article.title());
            }
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
    }

    public List<NoticiaModel> exibirNoticiasPorCategoriaEDiaAnterior(String nome) {
        return exibirNoticiasPorCategoriaEData(nome, calendario.ontem());
    }

    public List<NoticiaModel> exibirNoticiasPorCategoriaEData(String nome, LocalDate data) {
        return noticiaRepository.findByCategoriaNomeAndDataBetween(nome, calendario.inicio(data), calendario.fim(data));
    }
}
