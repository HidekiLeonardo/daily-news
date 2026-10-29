package com.hideki.daily_news.agendamento;

import com.hideki.daily_news.calendario.Calendario;
import com.hideki.daily_news.categoria.CategoriaModel;
import com.hideki.daily_news.categoria.CategoriaRepository;
import com.hideki.daily_news.noticia.NoticiaModel;
import com.hideki.daily_news.noticia.NoticiaService;
import com.hideki.daily_news.resumo.ResumoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AgendamentoService {

    private static final Logger log = LoggerFactory.getLogger(AgendamentoService.class);
    private final NoticiaService noticiaService;
    private final CategoriaRepository categoriaRepository;
    private final ResumoService resumoService;
    private final Calendario calendario;

    public AgendamentoService(NoticiaService noticiaService, CategoriaRepository categoriaRepository, ResumoService resumoService, Calendario calendario) {
        this.noticiaService = noticiaService;
        this.categoriaRepository = categoriaRepository;
        this.resumoService = resumoService;
        this.calendario = calendario;
    }

    @Scheduled(cron = "${cron.daily}")
    public void realizarTarefa() {
        LocalDate ontem = calendario.ontem();
        for (CategoriaModel categoria : categoriaRepository.findAll()) {
            List<NoticiaModel> noticias = noticiaService.exibirNoticiasPorCategoriaEDiaAnterior(categoria.getNome());
            try {
                noticiaService.buscarESalvar(categoria, ontem);
                resumoService.gerarResumo(categoria, ontem, noticias);
            } catch (Exception e) {
                log.error("Falhou ao processar a categoria {}", categoria.getNome(), e);
            }
        }
    }

}
