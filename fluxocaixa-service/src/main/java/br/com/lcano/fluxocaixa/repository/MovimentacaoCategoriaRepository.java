package br.com.lcano.fluxocaixa.repository;

import br.com.lcano.fluxocaixa.domain.MovimentacaoCategoria;
import br.com.lcano.fluxocaixa.enums.TipoCategoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovimentacaoCategoriaRepository extends JpaRepository<MovimentacaoCategoria, Long>, JpaSpecificationExecutor<MovimentacaoCategoria> {

    @Query("SELECT c FROM MovimentacaoCategoria c "
            + "WHERE (c.idUsuario = :idUsuario OR c.idUsuario IS NULL) "
            + "AND lower(c.descricao) = lower(:descricao) AND c.tipo = :tipo")
    Optional<MovimentacaoCategoria> findVisivelByDescricaoAndTipo(@Param("idUsuario") Long idUsuario,
                                                                 @Param("descricao") String descricao,
                                                                 @Param("tipo") TipoCategoria tipo);
}
