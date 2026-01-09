package com.banco.cast.controller;

import com.banco.cast.model.Conta;
import com.banco.cast.model.Usuario;
import com.banco.cast.service.BancoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contas")
@RequiredArgsConstructor
@Tag(name = "Contas", description = "API para operações bancárias de manipulação de contas")
@CrossOrigin(origins = "*")
public class ContaController {

    private final BancoService bancoService;

    @PostMapping
    @Operation(summary = "Criar conta", description = "Cria uma nova conta bancária a partir dos dados de um usuário.")
    public ResponseEntity<Conta> criar(@RequestBody Usuario.UsuarioRequest request) {
        return ResponseEntity.ok(bancoService.criarConta(request));
    }

    @PostMapping("/creditar")
    @Operation(summary = "Creditar valor", description = "Adiciona um valor ao saldo da conta informada.")
    public ResponseEntity<Void> creditar(@RequestBody Conta.OperacaoRequest request) {
        bancoService.creditar(request.numeroConta(), request.valor());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/debitar")
    @Operation(summary = "Debitar valor", description = "Remove um valor do saldo da conta, se houver saldo suficiente.")
    public ResponseEntity<Void> debitar(@RequestBody Conta.OperacaoRequest request) {
        bancoService.debitar(request.numeroConta(), request.valor());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/transferir")
    @Operation(summary = "Transferir valores", description = "Realiza a transferência de valores entre duas contas distintas.")
    public ResponseEntity<Void> transferir(@RequestBody Conta.TransferenciaRequest request) {
        bancoService.transferir(request.contaOrigem(), request.contaDestino(), request.valor());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/extrato")
    @Operation(summary = "Consultar extrato", description = "Retorna o saldo e informações da conta.")
    public ResponseEntity<Conta.ExtratoResponse> extrato(@RequestBody Conta.ExtratoRequest request) {
        return ResponseEntity.ok(bancoService.emitirExtrato(request.numeroConta()));
    }
}