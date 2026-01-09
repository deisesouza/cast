package com.banco.cast.service;

import com.banco.cast.model.Conta;
import com.banco.cast.model.Usuario;

public interface BancoService {

    Conta criarConta(Usuario.UsuarioRequest request);
    void creditar(Long id, Double valor);
    void debitar(Long id, Double valor);
    void transferir(Long origemId, Long destinoId, Double valor);
    Conta.ExtratoResponse emitirExtrato(Long id);
}
