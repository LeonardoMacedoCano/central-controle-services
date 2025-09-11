package br.com.lcano.usuario.repository;

import br.com.lcano.usuario.domain.Tema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TemaRepository extends JpaRepository<Tema, Long> {
    List<Tema> findByUsuarioIdOrUsuarioIsNull(Long id);

    Optional<Tema> findByIsDefault(boolean isDefault);
}