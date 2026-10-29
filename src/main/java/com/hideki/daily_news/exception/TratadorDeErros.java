package com.hideki.daily_news.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TratadorDeErros {
    @ExceptionHandler(CategoriaNaoExistenteException.class)
    public ResponseEntity<?> naoEncontrado(CategoriaNaoExistenteException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
