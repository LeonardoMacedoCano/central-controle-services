package br.com.lcano.fluxocaixa.repository;

import br.com.lcano.fluxocaixa.domain.RegraExtratoContaCorrente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegraExtratoContaCorrenteRepository extends JpaRepository<RegraExtratoContaCorrente, Long>, JpaSpecificationExecutor<RegraExtratoContaCorrente> {
    List<RegraExtratoContaCorrente> findByIdUsuarioAndAtivoOrderByPrioridadeAsc(Long idUsuario, Boolean ativo);
}