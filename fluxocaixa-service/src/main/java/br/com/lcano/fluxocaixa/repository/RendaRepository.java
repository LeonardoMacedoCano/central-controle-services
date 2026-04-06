package br.com.lcano.fluxocaixa.repository;

import br.com.lcano.fluxocaixa.domain.Renda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface RendaRepository extends JpaRepository<Renda, Long> {

    Optional<Renda> findByLancamentoId(Long lancamentoId);

    @Query("SELECT COALESCE(SUM(r.valor), 0) FROM Renda r JOIN r.lancamento l " +
           "WHERE l.idUsuario = :idUsuario AND r.dataRecebimento >= :inicio AND r.dataRecebimento < :fim")
    BigDecimal sumValorByPeriodo(@Param("idUsuario") Long idUsuario,
                                 @Param("inicio") Date inicio,
                                 @Param("fim") Date fim);

    @Query("SELECT r.categoria.descricao, SUM(r.valor) FROM Renda r JOIN r.lancamento l " +
           "WHERE l.idUsuario = :idUsuario AND r.dataRecebimento >= :inicio AND r.dataRecebimento < :fim " +
           "GROUP BY r.categoria.descricao ORDER BY SUM(r.valor) DESC")
    List<Object[]> sumValorByCategoriaAndPeriodo(@Param("idUsuario") Long idUsuario,
                                                 @Param("inicio") Date inicio,
                                                 @Param("fim") Date fim);
}
