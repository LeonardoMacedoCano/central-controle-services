package br.com.lcano.fluxocaixa.repository;

import br.com.lcano.fluxocaixa.domain.MapeamentoExtratoBancario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MapeamentoExtratoBancarioRepository extends JpaRepository<MapeamentoExtratoBancario, Long>, JpaSpecificationExecutor<MapeamentoExtratoBancario> {
    List<MapeamentoExtratoBancario> findByIdUsuarioAndAtivoOrderByPrioridadeAsc(Long idUsuario, Boolean ativo);
}
