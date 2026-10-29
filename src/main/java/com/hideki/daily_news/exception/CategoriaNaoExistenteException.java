package com.hideki.daily_news.exception;

public class CategoriaNaoExistenteException extends RuntimeException {
    public CategoriaNaoExistenteException(String message) {
        super(message);
    }
}
