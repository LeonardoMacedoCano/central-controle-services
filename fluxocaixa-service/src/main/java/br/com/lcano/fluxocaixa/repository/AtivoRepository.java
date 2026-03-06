package br.com.lcano.fluxocaixa.repository;

import br.com.lcano.fluxocaixa.domain.Ativo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AtivoRepository extends JpaRepository<Ativo, Long> {
    Optional<Ativo> findByLancamentoId(Long lancamentoId);
}
