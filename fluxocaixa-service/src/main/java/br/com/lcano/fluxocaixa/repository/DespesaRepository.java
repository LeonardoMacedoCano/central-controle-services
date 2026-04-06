package br.com.lcano.fluxocaixa.repository;

import br.com.lcano.fluxocaixa.domain.Despesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface DespesaRepository extends JpaRepository<Despesa, Long> {

    Optional<Despesa> findByLancamentoId(Long lancamentoId);

    @Query("SELECT COALESCE(SUM(d.valor), 0) FROM Despesa d JOIN d.lancamento l " +
           "WHERE l.idUsuario = :idUsuario AND d.dataVencimento >= :inicio AND d.dataVencimento < :fim")
    BigDecimal sumValorByPeriodo(@Param("idUsuario") Long idUsuario,
                                 @Param("inicio") Date inicio,
                                 @Param("fim") Date fim);

    @Query("SELECT d.categoria.descricao, SUM(d.valor) FROM Despesa d JOIN d.lancamento l " +
           "WHERE l.idUsuario = :idUsuario AND d.dataVencimento >= :inicio AND d.dataVencimento < :fim " +
           "GROUP BY d.categoria.descricao ORDER BY SUM(d.valor) DESC")
    List<Object[]> sumValorByCategoriaAndPeriodo(@Param("idUsuario") Long idUsuario,
                                                 @Param("inicio") Date inicio,
                                                 @Param("fim") Date fim);
}
