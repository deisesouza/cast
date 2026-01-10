package com.banco.cast.service;

import com.banco.cast.model.Conta;
import com.banco.cast.model.Usuario;
import com.banco.cast.repository.ContaRepository;
import com.banco.cast.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class BancoServiceImpl implements BancoService {

    private final ContaRepository contaRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public Conta criarConta(Usuario.UsuarioRequest request) {
        Usuario usuario = new Usuario(request.nome(), request.senha(), request.admin());
        usuario = usuarioRepository.save(usuario);

        String numeroConta = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 999999));
        String agencia = String.valueOf(ThreadLocalRandom.current().nextInt(1000, 9999));

        // Inicializando com BigDecimal.ZERO
        Conta conta = new Conta(null, numeroConta, agencia, BigDecimal.ZERO, usuario);
        return contaRepository.save(conta);
    }

    @Override
    @Transactional
    @Retryable(
            retryFor = { ObjectOptimisticLockingFailureException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 100)
    )
    public void creditar(String numeroConta, BigDecimal valor) {
        BigDecimal valorBD = valor;
        Conta conta = this.validarContaPorNumero(numeroConta);

        // Uso de .add() para somar BigDecimal
        conta.setSaldo(conta.getSaldo().add(valorBD));

        contaRepository.save(conta);
    }

    @Override
    @Transactional
    @Retryable(
            retryFor = { ObjectOptimisticLockingFailureException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 100)
    )
    public void debitar(String numeroConta, BigDecimal valor) {
        BigDecimal valorBD = valor;
        Conta conta = this.validarContaPorNumero(numeroConta);

        validarSaldo(conta, valorBD);

        // Uso de .subtract() para subtrair BigDecimal
        conta.setSaldo(conta.getSaldo().subtract(valorBD));

        contaRepository.save(conta);
    }

    @Override
    @Transactional
    @Retryable(
            retryFor = { ObjectOptimisticLockingFailureException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 100)
    )
    public void transferir(String contaOrigem, String contaDestino, BigDecimal valor) {
        // 1. Busca as duas contas (carrega as versões atuais)
        Conta origem = this.validarContaPorNumero(contaOrigem);
        validarSaldo(origem, valor);
        Conta destino = this.validarContaPorNumero(contaDestino);

        // 2. Modifica os estados
        origem.setSaldo(origem.getSaldo().subtract(valor));
        destino.setSaldo(destino.getSaldo().add(valor));

        // 3. Ao finalizar o metodo, o JPA fara o flush.
        // Se alguém alterou a conta 'origem' ou 'destino' enquanto este metodo rodava,
        // uma ObjectOptimisticLockingFailureException será lançada aqui.
        contaRepository.save(origem);
        contaRepository.save(destino);
    }

    @Override
    public Conta.ExtratoResponse emitirExtrato(String numeroConta) {
        Conta conta = this.validarContaPorNumero(numeroConta);
        // Convertendo BigDecimal para Double apenas na resposta se o Record exigir
        return new Conta.ExtratoResponse(
                conta.getTitular().getNome(),
                conta.getNumeroConta(),
                conta.getAgencia(),
                conta.getSaldo()
        );
    }

    private Conta validarContaPorNumero(String numeroConta) {
        Conta conta = contaRepository.findByNumeroConta(numeroConta);
        if (conta == null) {
            throw new EntityNotFoundException("Conta " + numeroConta + " não localizada.");
        }
        return conta;
    }

    private void validarSaldo(Conta conta, BigDecimal valor) {
        // compareTo retorna -1 se o saldo for menor que o valor
        if (conta.getSaldo().compareTo(valor) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente. Saldo atual: " + conta.getSaldo());
        }
    }
}