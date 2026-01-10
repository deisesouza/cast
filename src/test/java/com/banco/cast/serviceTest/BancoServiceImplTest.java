package com.banco.cast.serviceTest;

import com.banco.cast.model.Conta;
import com.banco.cast.model.Usuario;
import com.banco.cast.repository.ContaRepository;
import com.banco.cast.repository.UsuarioRepository;
import com.banco.cast.service.BancoServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BancoServiceImplTest {

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private BancoServiceImpl bancoService;

    @Nested
    @DisplayName("Testes de Criação de Conta")
    class CriarContaTests {
        @Test
        @DisplayName("Deve criar um usuário e uma conta com saldo zero com sucesso")
        void deveCriarContaComSucesso() {
            Usuario.UsuarioRequest request = new Usuario.UsuarioRequest("João Silva", "senha123", false);
            Usuario usuarioSalvo = new Usuario("João Silva", "senha123", false);

            when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioSalvo);
            when(contaRepository.save(any(Conta.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Conta contaCriada = bancoService.criarConta(request);

            assertNotNull(contaCriada);
            assertEquals(BigDecimal.ZERO, contaCriada.getSaldo());
            assertEquals(usuarioSalvo, contaCriada.getTitular());
            verify(usuarioRepository, times(1)).save(any(Usuario.class));
            verify(contaRepository, times(1)).save(any(Conta.class));
        }
    }

    @Nested
    @DisplayName("Testes de Operações Financeiras")
    class OperacoesFinanceirasTests {

        @Test
        @DisplayName("Deve creditar valor em uma conta existente")
        void deveCreditarComSucesso() {
            String numConta = "123456";
            Conta conta = new Conta(1L, numConta, "0001", new BigDecimal("100.00"), new Usuario());

            when(contaRepository.findByNumeroConta(numConta)).thenReturn(conta);

            bancoService.creditar(numConta, new BigDecimal("50.00"));

            assertEquals(new BigDecimal("150.00"), conta.getSaldo());
            verify(contaRepository).save(conta);
        }

        @Test
        @DisplayName("Deve debitar valor quando houver saldo suficiente")
        void deveDebitarComSucesso() {
            String numConta = "123456";
            Conta conta = new Conta(1L, numConta, "0001", new BigDecimal("100.00"), new Usuario());

            when(contaRepository.findByNumeroConta(numConta)).thenReturn(conta);

            bancoService.debitar(numConta, new BigDecimal("40.00"));

            assertEquals(new BigDecimal("60.00"), conta.getSaldo());
            verify(contaRepository).save(conta);
        }

        //para seguir a boa prática de "Single Action Lambda", garantimos que nenhuma outra operação
        // possa acidentalmente lançar uma RuntimeException antes da chamada principal.
        @Test
        @DisplayName("Deve lançar exceção ao debitar valor maior que o saldo")
        void deveLancarExcecaoSaldoInsuficiente() {
            // Arrange (Preparação)
            String numConta = "123456";
            BigDecimal valorDebito = new BigDecimal("50.00");
            Conta conta = new Conta(1L, numConta, "0001", new BigDecimal("30.00"), new Usuario());

            when(contaRepository.findByNumeroConta(numConta)).thenReturn(conta);

            // Act & Assert (Ação e Validação)
            // O lambda agora contém apenas a invocação do método alvo
            assertThrows(IllegalArgumentException.class, () -> bancoService.debitar(numConta, valorDebito));

            // Verificação adicional (Opcional, mas recomendada)
            verify(contaRepository, never()).save(any(Conta.class));
        }
    }

    @Nested
    @DisplayName("Testes de Transferência")
    class TransferenciaTests {

        @Test
        @DisplayName("Deve transferir valores entre contas com sucesso")
        void deveTransferirComSucesso() {
            Conta origem = new Conta(1L, "111", "001", new BigDecimal("1000.00"), new Usuario());
            Conta destino = new Conta(2L, "222", "001", new BigDecimal("500.00"), new Usuario());

            when(contaRepository.findByNumeroConta("111")).thenReturn(origem);
            when(contaRepository.findByNumeroConta("222")).thenReturn(destino);

            bancoService.transferir("111", "222", new BigDecimal("200.00"));

            assertEquals(new BigDecimal("800.00"), origem.getSaldo());
            assertEquals(new BigDecimal("700.00"), destino.getSaldo());
            verify(contaRepository, times(2)).save(any(Conta.class));
        }
    }

    @Nested
    @DisplayName("Testes de Validação e Extrato")
    class ValidacaoTests {

        @Test
        @DisplayName("Deve lançar EntityNotFoundException quando conta não existir")
        void deveLancarErroContaNaoEncontrada() {
            when(contaRepository.findByNumeroConta("999")).thenReturn(null);

            assertThrows(EntityNotFoundException.class, () ->
                    bancoService.emitirExtrato("999")
            );
        }

        @Test
        @DisplayName("Deve retornar extrato formatado corretamente")
        void deveEmitirExtrato() {
            Usuario user = new Usuario("Maria", "123", false);
            Conta conta = new Conta(1L, "123", "001", new BigDecimal("150.75"), user);

            when(contaRepository.findByNumeroConta("123")).thenReturn(conta);

            Conta.ExtratoResponse response = bancoService.emitirExtrato("123");

            assertEquals("Maria", response.titular());
            assertEquals(new BigDecimal("150.75"), response.saldo());
        }
    }
}