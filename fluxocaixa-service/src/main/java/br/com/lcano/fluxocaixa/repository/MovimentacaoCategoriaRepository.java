package br.com.lcano.fluxocaixa.repository;

import br.com.lcano.fluxocaixa.domain.MovimentacaoCategoria;
import br.com.lcano.fluxocaixa.enums.TipoCategoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovimentacaoCategoriaRepository extends JpaRepository<MovimentacaoCategoria, Long>, JpaSpecificationExecutor<MovimentacaoCategoria> {

    Optional<MovimentacaoCategoria> findByDescricaoIgnoreCaseAndTipo(String descricao, TipoCategoria tipo);
}
