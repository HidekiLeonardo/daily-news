package com.hideki.daily_news.feed;

import com.hideki.daily_news.categoria.dto.CategoriaResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/daily-news")
public class FeedController {
    private final FeedService service;

    public FeedController(FeedService service) {
        this.service = service;
    }

    @GetMapping("/{categoria}")
    public FeedResponse feed(@PathVariable String categoria) {
        return service.montarFeed(categoria);
    }

    @GetMapping("/categorias")
    public List<CategoriaResponse> categorias() {
        return service.categoriasExistentes();
    }
}
