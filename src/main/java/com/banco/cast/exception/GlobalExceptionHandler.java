package com.banco.cast.exception;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.persistence.EntityNotFoundException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. TRATAMENTO PARA BLOQUEIO OTIMISTA (CONCORRÊNCIA)
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<?> handleOptimisticLock(ObjectOptimisticLockingFailureException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "erro", "Conflito de Atualização",
                "mensagem", "Esta conta foi alterada por outra transação. Por favor, tente novamente."
        ));
    }

    // 2. TRATAMENTO PARA SALDO INSUFICIENTE
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "erro", "Operação Inválida",
                "mensagem", ex.getMessage()
        ));
    }

    // 3. TRATAMENTO PARA CONTA NÃO ENCONTRADA
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "erro", "Recurso não encontrado",
                "mensagem", ex.getMessage()
        ));
    }
}