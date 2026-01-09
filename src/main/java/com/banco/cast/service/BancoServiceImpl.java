package com.banco.cast.service;

import com.banco.cast.model.Conta;
import com.banco.cast.model.Usuario;
import com.banco.cast.repository.ContaRepository;
import com.banco.cast.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BancoServiceImpl implements BancoService {

    private final ContaRepository contaRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public Conta criarConta(Usuario.UsuarioRequest request) {
        Usuario usuario = new Usuario(request.nome(), request.senha(), request.admin());
        usuario = usuarioRepository.save(usuario);
        return contaRepository.save(new Conta(null, 0.0, usuario));
    }

    @Override
    public void creditar(Long id, Double valor) {
        Conta conta = this.validarConta(id);
        conta.setSaldo(conta.getSaldo() + valor);
        contaRepository.save(conta);
    }

    @Override
    public void debitar(Long id, Double valor) {
        Conta conta = this.validarConta(id);
        validarSaldo(conta, valor);
        conta.setSaldo(conta.getSaldo() - valor);
        contaRepository.save(conta);
    }

    @Override
    public void transferir(Long origemId, Long destinoId, Double valor) {
        this.debitar(origemId, valor);
        this.creditar(destinoId, valor);
    }

    @Override
    public Conta.ExtratoResponse emitirExtrato(Long id) {
        Conta conta = this.validarConta(id);
        return new Conta.ExtratoResponse(conta.getId(), conta.getTitular().getNome(), conta.getSaldo());
    }

    public void validarSaldo(Conta conta, Double valor){
        if(conta.getSaldo() < valor){
            throw new IllegalArgumentException("Saldo insuficiente para a operação");
        }
    }

    public Conta validarConta(Long id){
        return contaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conta não existe."));
    }
}
