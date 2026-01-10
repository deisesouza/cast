package com.banco.cast.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numeroConta;
    private String agencia;

    // Definindo precisão (19 dígitos) e escala (2 casas decimais) para o banco de dados
    @Column(precision = 19, scale = 2)
    private BigDecimal saldo = BigDecimal.ZERO;

    @Version
    private Long version;

    @OneToOne
    private Usuario titular;

    public Conta(Long id, String numeroConta, String agencia, BigDecimal saldo, Usuario titular) {
        this.id = id;
        this.numeroConta = numeroConta;
        this.agencia = agencia;
        // Garante que não iniciaremos com null se o saldo for passado vazio
        this.saldo = (saldo != null) ? saldo : BigDecimal.ZERO;
        this.titular = titular;
    }

    // Records atualizados para BigDecimal para manter a precisão na API
    public record ExtratoRequest(String numeroConta, String agencia) {}

    public record ExtratoResponse(
            String titular,
            String numeroConta,
            String agencia,
            BigDecimal saldo
    ) {}

    public record OperacaoRequest(
            String numeroConta,
            String agencia,
            BigDecimal valor
    ) {}

    public record TransferenciaRequest(
            String contaOrigem,
            String contaDestino,
            BigDecimal valor
    ) {}
}