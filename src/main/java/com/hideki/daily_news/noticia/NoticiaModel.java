package com.hideki.daily_news.noticia;

import com.hideki.daily_news.categoria.CategoriaModel;
import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "noticia")
public class NoticiaModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private CategoriaModel categoria;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String titulo;
    @Column(nullable = false)
    private OffsetDateTime data;
    @Column(unique = true, nullable = false, columnDefinition = "TEXT")
    private String url;
    @Column(columnDefinition = "TEXT", nullable = false)
    private String descricao;
    @Column(columnDefinition = "TEXT")
    private String imagem;
    @Column(nullable = false)
    private String fonte;

    public NoticiaModel(Long id, CategoriaModel categoria, String titulo, OffsetDateTime data, String url, String descricao, String imagem, String fonte) {
        this.id = id;
        this.categoria = categoria;
        this.titulo = titulo;
        this.data = data;
        this.url = url;
        this.descricao = descricao;
        this.imagem = imagem;
        this.fonte = fonte;
    }

    public NoticiaModel() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CategoriaModel getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaModel categoria) {
        this.categoria = categoria;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public OffsetDateTime getData() {
        return data;
    }

    public void setData(OffsetDateTime data) {
        this.data = data;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public String getFonte() {
        return fonte;
    }

    public void setFonte(String fonte) {
        this.fonte = fonte;
    }
}
