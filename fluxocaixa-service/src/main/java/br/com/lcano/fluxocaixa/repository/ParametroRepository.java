package br.com.lcano.fluxocaixa.repository;

import br.com.lcano.fluxocaixa.domain.Parametro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParametroRepository extends JpaRepository<Parametro, Long> {
    Parametro findByIdUsuario(Long idUsuario);
}
