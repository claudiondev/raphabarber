package com.claudio.dev.raphabarber.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AcessoNegadoException.class)
    public ResponseEntity<Map<String, String>> handleAcessoNegado(AcessoNegadoException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("erro", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidacao(MethodArgumentNotValidException e) {
        Map<String, String> erros = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(erro ->
                erros.put(erro.getField(), erro.getDefaultMessage()));
        return ResponseEntity.badRequest().body(erros);
    }

    // Última linha de defesa: qualquer exceção não tratada explicitamente não deve vazar
    // stack trace ou mensagem interna (ex: erro de SQL, NullPointerException) para o cliente.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeral(Exception e) {
        log.error("Erro não tratado", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("erro", "Ocorreu um erro inesperado. Tente novamente mais tarde."));
    }
}
