package br.com.lcano.fluxocaixa.repository;

import br.com.lcano.fluxocaixa.domain.MovimentacaoCategoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MovimentacaoCategoriaRepository extends JpaRepository<MovimentacaoCategoria, Long>, JpaSpecificationExecutor<MovimentacaoCategoria> {
}
