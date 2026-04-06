package br.com.lcano.fluxocaixa.repository;

import br.com.lcano.fluxocaixa.domain.Ativo;
import br.com.lcano.fluxocaixa.enums.TipoOperacaoExtratoMovimentacaoB3;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface AtivoRepository extends JpaRepository<Ativo, Long> {

    Optional<Ativo> findByLancamentoId(Long lancamentoId);

    @Query("SELECT COALESCE(SUM(a.valor), 0) FROM Ativo a JOIN a.lancamento l " +
           "WHERE l.idUsuario = :idUsuario AND a.operacao = :operacao " +
           "AND a.dataMovimento >= :inicio AND a.dataMovimento < :fim")
    BigDecimal sumValorByOperacaoAndPeriodo(@Param("idUsuario") Long idUsuario,
                                            @Param("operacao") TipoOperacaoExtratoMovimentacaoB3 operacao,
                                            @Param("inicio") Date inicio,
                                            @Param("fim") Date fim);

    @Query("SELECT a.categoria.descricao, a.operacao, SUM(a.valor) FROM Ativo a JOIN a.lancamento l " +
           "WHERE l.idUsuario = :idUsuario AND a.dataMovimento >= :inicio AND a.dataMovimento < :fim " +
           "GROUP BY a.categoria.descricao, a.operacao ORDER BY a.categoria.descricao")
    List<Object[]> sumValorByCategoriaAndOperacaoAndPeriodo(@Param("idUsuario") Long idUsuario,
                                                            @Param("inicio") Date inicio,
                                                            @Param("fim") Date fim);
}
