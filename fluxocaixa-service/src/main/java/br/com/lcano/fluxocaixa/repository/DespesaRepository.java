package br.com.lcano.fluxocaixa.repository;

import br.com.lcano.fluxocaixa.domain.Despesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DespesaRepository extends JpaRepository<Despesa, Long> {
    Optional<Despesa> findByLancamentoId(Long lancamentoId);
}
