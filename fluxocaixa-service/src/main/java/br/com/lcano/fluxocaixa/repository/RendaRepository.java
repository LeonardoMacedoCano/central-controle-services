package br.com.lcano.fluxocaixa.repository;

import br.com.lcano.fluxocaixa.domain.Renda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RendaRepository extends JpaRepository<Renda, Long> {
    Optional<Renda> findByLancamentoId(Long lancamentoId);
}
