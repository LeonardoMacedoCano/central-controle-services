package br.com.lcano.fluxocaixa.repository;

import br.com.lcano.fluxocaixa.domain.ImportacaoExtrato;
import br.com.lcano.fluxocaixa.enums.StatusImportacaoExtrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImportacaoExtratoRepository extends JpaRepository<ImportacaoExtrato, Long> {

    Optional<ImportacaoExtrato> findByIdUsuarioAndHashArquivoAndStatus(Long idUsuario, String hashArquivo, StatusImportacaoExtrato status);
}
