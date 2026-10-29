package com.hideki.daily_news.resumo;

import com.hideki.daily_news.categoria.CategoriaModel;
import com.hideki.daily_news.noticia.NoticiaModel;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ResumoService {
    private final ResumoRepository resumoRepository;
    private final GroqClient groqClient;

    public ResumoService(ResumoRepository resumoRepository, GroqClient groqClient) {
        this.resumoRepository = resumoRepository;
        this.groqClient = groqClient;
    }

    public void gerarResumo(CategoriaModel categoriaModel, LocalDate dia, List<NoticiaModel> noticias) {
        String categoria = categoriaModel.getNome();
        if (!resumoRepository.existsByCategoriaNomeAndData(categoria, dia)){
            StringBuilder prompt = new StringBuilder();
            prompt.append("Você é um assistente especializado em notícias.\n");
            prompt.append("Sintetize, em um único parágrafo, o que aconteceu neste assunto, sem preâmbulo\n");
            prompt.append("Utilize linguagem clara, objetiva e imparcial.\n");
            prompt.append("Não invente informações que não estejam no texto original.\n");
            prompt.append("No máximo 500 caracteres\n");
            prompt.append("NOTÍCIAS:\n");
            for (NoticiaModel n : noticias) {
                prompt.append("Título: ")
                        .append(n.getTitulo())
                        .append("\n");
                prompt.append("Conteúdo: ")
                        .append(n.getDescricao())
                        .append("\n");
                prompt.append("Fonte: ")
                        .append(n.getFonte())
                        .append("\n");
            }
            String p = prompt.toString();

            ResumoModel resumo = new ResumoModel();
            resumo.setCategoria(categoriaModel);
            resumo.setData(dia);
            resumo.setTexto(groqClient.resumir(p));
            resumoRepository.save(resumo);
        }
    }

    public String buscarResumo(String categoria, LocalDate data) {
        return resumoRepository.findByCategoriaNomeAndData(categoria, data)
                .map(ResumoModel::getTexto)
                .orElse("Não há resumo para a categoria " + categoria + " no dia " + data + "...");
    }
}
