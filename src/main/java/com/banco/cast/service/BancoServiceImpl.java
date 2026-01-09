package com.banco.cast.service;

import com.banco.cast.model.Conta;
import com.banco.cast.model.Usuario;
import com.banco.cast.repository.ContaRepository;
import com.banco.cast.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class BancoServiceImpl implements BancoService {

    private final ContaRepository contaRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public Conta criarConta(Usuario.UsuarioRequest request) {
        Usuario usuario = new Usuario(request.nome(), request.senha(), request.admin());
        usuario = usuarioRepository.save(usuario);
        String numeroConta = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 999999));
        String agencia = String.valueOf(ThreadLocalRandom.current().nextInt(1000, 9999));
        Conta conta = new Conta(null, numeroConta, agencia, 0.0, usuario);
        return contaRepository.save(conta);
    }

    @Override
    @Transactional
    public void creditar(String numeroConta, Double valor){
        Conta conta = this.validarContaPorNumero(numeroConta);
        conta.setSaldo(conta.getSaldo() + valor);
        contaRepository.save(conta);
    }

    @Override
    public void debitar(String numeroConta, Double valor) {
        Conta conta = this.validarContaPorNumero(numeroConta);
        validarSaldo(conta, valor);
        conta.setSaldo(conta.getSaldo() - valor);
        contaRepository.save(conta);
    }

    @Override
    public void transferir(String contaOrigem, String contaDestino, Double valor) {
        Conta origem = this.validarContaPorNumero(contaOrigem);
        validarSaldo(origem, valor);
        Conta destino = this.validarContaPorNumero(contaDestino);
        this.debitar(origem.getNumeroConta(), valor);
        this.creditar(destino.getNumeroConta(), valor);
    }

    @Override
    public Conta.ExtratoResponse emitirExtrato(String numeroConta) {
        Conta conta = this.validarContaPorNumero(numeroConta);
        return new Conta.ExtratoResponse(conta.getTitular().getNome(), conta.getNumeroConta(), conta.getAgencia(), conta.getSaldo());
    }

    private Conta validarContaPorNumero(String numeroConta) {
        return contaRepository.findByNumeroConta(numeroConta);
    }

    private void validarSaldo(Conta conta, Double valor){
        if(conta.getSaldo() < valor){
            throw new IllegalArgumentException("Saldo insuficiente para a operação");
        }
    }
}
