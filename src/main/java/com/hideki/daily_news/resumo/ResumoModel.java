package com.hideki.daily_news.resumo;

import com.hideki.daily_news.categoria.CategoriaModel;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "resumo", uniqueConstraints = @UniqueConstraint(columnNames = {"categoria_id", "data"}))
public class ResumoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private CategoriaModel categoria;

    @Column(nullable = false)
    private LocalDate data;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String texto;

    public ResumoModel(Long id, CategoriaModel categoria, LocalDate data, String texto) {
        this.id = id;
        this.categoria = categoria;
        this.data = data;
        this.texto = texto;
    }

    public ResumoModel() {
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

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }
}
