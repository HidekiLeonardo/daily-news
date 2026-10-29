package com.hideki.daily_news.feed;

import com.hideki.daily_news.exception.CategoriaNaoExistenteException;
import com.hideki.daily_news.calendario.Calendario;
import com.hideki.daily_news.categoria.CategoriaMapper;
import com.hideki.daily_news.categoria.CategoriaRepository;
import com.hideki.daily_news.categoria.dto.CategoriaResponse;
import com.hideki.daily_news.noticia.NoticiaMapper;
import com.hideki.daily_news.noticia.NoticiaModel;
import com.hideki.daily_news.noticia.NoticiaService;
import com.hideki.daily_news.noticia.dto.NoticiaResponse;
import com.hideki.daily_news.resumo.ResumoService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class FeedService {
    private final NoticiaService noticiaService;
    private final ResumoService resumoService;
    private final NoticiaMapper noticiaMapper;
    private final Calendario calendario;
    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    public FeedService(NoticiaService noticiaService, ResumoService resumoService, NoticiaMapper noticiaMapper, Calendario calendario, CategoriaRepository categoriaRepository, CategoriaMapper categoriaMapper) {
        this.noticiaService = noticiaService;
        this.resumoService = resumoService;
        this.noticiaMapper = noticiaMapper;
        this.calendario = calendario;
        this.categoriaRepository = categoriaRepository;
        this.categoriaMapper = categoriaMapper;
    }

    public FeedResponse montarFeed(String categoria) {
        if (!categoriaRepository.existsByNomeIgnoreCase(categoria)){
            throw new CategoriaNaoExistenteException("Categoria " + categoria + " não existe...");
        }

        LocalDate ontem = calendario.ontem();
        List<NoticiaModel> noticias = noticiaService.exibirNoticiasPorCategoriaEData(categoria, ontem);
        String resumo = resumoService.buscarResumo(categoria, ontem);
        List<NoticiaResponse> noticiasResponse = noticias.stream()
                .map(noticiaMapper::toResponse)
                .toList();

        return new FeedResponse(categoria, resumo, noticiasResponse);
    }

    public List<CategoriaResponse> categoriasExistentes() {
        return categoriaRepository.findAll().stream()
                .map(categoriaMapper::toDTO)
                .toList();
    }
}
