package br.com.lcano.usuario.repository;

import br.com.lcano.usuario.domain.Notificacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {

    Page<Notificacao> findByIdUsuarioOrderByDataCriacaoDesc(Long idUsuario, Pageable pageable);

    Page<Notificacao> findByIdUsuarioAndLidaOrderByDataCriacaoDesc(Long idUsuario, boolean lida, Pageable pageable);

    long countByIdUsuarioAndLida(Long idUsuario, boolean lida);

    List<Notificacao> findByIdUsuarioAndLida(Long idUsuario, boolean lida);
}
