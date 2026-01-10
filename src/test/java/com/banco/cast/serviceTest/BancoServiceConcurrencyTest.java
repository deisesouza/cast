package com.banco.cast.serviceTest;

import com.banco.cast.model.Conta;
import com.banco.cast.model.Usuario;
import com.banco.cast.repository.ContaRepository;
import com.banco.cast.repository.UsuarioRepository;
import com.banco.cast.service.BancoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class BancoServiceConcurrencyTest {

    @Autowired
    private BancoService bancoService;

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private String numeroContaTeste;

    @BeforeEach
    void setUp() {
        contaRepository.deleteAll();
        usuarioRepository.deleteAll();

        // Cria uma conta inicial para os testes
        Usuario.UsuarioRequest request = new Usuario.UsuarioRequest("Admin", "123", true);
        Conta conta = bancoService.criarConta(request);
        numeroContaTeste = conta.getNumeroConta();

        // Define saldo inicial de 1000
        bancoService.creditar(numeroContaTeste, new BigDecimal("1000.00"));
    }

    @Test
    @DisplayName("Deve lidar com múltiplas atualizações simultâneas usando Retry")
    void deveLidarComConcorrenciaUsandoRetry() throws InterruptedException {
        int numThreads = 2;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);

        // Tentaremos subtrair 100 reais em duas threads ao mesmo tempo
        // O Optimistic Lock causará erro em uma delas, mas o @Retryable deve salvá-la
        executor.submit(() -> {
            try {
                bancoService.debitar(numeroContaTeste, new BigDecimal("100.00"));
            } finally {
                latch.countDown();
            }
        });

        executor.submit(() -> {
            try {
                bancoService.debitar(numeroContaTeste, new BigDecimal("100.00"));
            } finally {
                latch.countDown();
            }
        });

        latch.await(); // Espera ambas as threads terminarem

        // Se o Retry funcionou, o saldo deve ser exatamente 800 (1000 - 100 - 100)
        Conta contaFinal = contaRepository.findByNumeroConta(numeroContaTeste);
        assertEquals(new BigDecimal("800.00"), contaFinal.getSaldo().setScale(2));
    }
}