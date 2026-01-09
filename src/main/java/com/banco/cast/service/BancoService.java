package com.banco.cast.service;

import com.banco.cast.model.Conta;
import com.banco.cast.model.Usuario;

public interface BancoService {

    Conta criarConta(Usuario.UsuarioRequest request);
    void creditar(String numeroConta, Double valor);
    void debitar(String numeroConta, Double valor);
    void transferir(String contaOrigem, String contaDestino, Double valor);
    Conta.ExtratoResponse emitirExtrato(String numeroConta);
}
