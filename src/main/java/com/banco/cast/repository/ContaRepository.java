package com.banco.cast.repository;

import com.banco.cast.model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {
    Conta findByNumeroConta(String numeroConta);
}
