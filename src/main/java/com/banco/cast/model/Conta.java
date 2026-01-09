package com.banco.cast.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double saldo = 0.0;

    @OneToOne
    private Usuario titular;


    public record ExtratoResponse(Long contaId, String titular, Double saldo) {}
    public record OperacaoRequest(Long contaId, Double valor) {}
    public record TransferenciaRequest(Long origemId, Long destinoId, Double valor) {}
}