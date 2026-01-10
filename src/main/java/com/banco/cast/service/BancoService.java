package com.banco.cast.service;

import com.banco.cast.model.Conta;
import com.banco.cast.model.Usuario;

import java.math.BigDecimal;

public interface BancoService {

    Conta criarConta(Usuario.UsuarioRequest request);
    void creditar(String numeroConta, BigDecimal valor);
    void debitar(String numeroConta, BigDecimal valor);
    void transferir(String contaOrigem, String contaDestino, BigDecimal valor);
    Conta.ExtratoResponse emitirExtrato(String numeroConta);
}
