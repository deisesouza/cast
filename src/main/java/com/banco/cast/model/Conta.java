package com.banco.cast.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private Double saldo = 0.0;

    @OneToOne
    private Usuario titular;

    public Conta(Long id, String numeroConta, String agencia, Double saldo, Usuario titular) {
        this.id = id;
        this.numeroConta = numeroConta;
        this.agencia = agencia;
        this.saldo = saldo;
        this.titular = titular;
    }

    public record ExtratoRequest(String numeroConta, String agencia) {}
    public record ExtratoResponse(String titular, String numeroConta, String agencia, Double saldo) {}
    public record OperacaoRequest(String numeroConta, String agencia, Double valor) {}
    public record TransferenciaRequest(String contaOrigem, String contaDestino, Double valor) {}
}