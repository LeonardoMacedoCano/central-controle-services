package br.com.lcano.fluxocaixa.service;

import br.com.lcano.fluxocaixa.domain.ImportacaoExtrato;
import br.com.lcano.fluxocaixa.domain.Parametro;
import br.com.lcano.fluxocaixa.dto.ImportacaoExtratoDTO;
import br.com.lcano.fluxocaixa.dto.LancamentoDTO;
import br.com.lcano.fluxocaixa.enums.StatusImportacaoExtrato;
import br.com.lcano.fluxocaixa.enums.TipoImportacaoExtrato;
import br.com.lcano.fluxocaixa.exception.ExtratoException;
import br.com.lcano.fluxocaixa.exception.LancamentoException;
import br.com.lcano.fluxocaixa.repository.ImportacaoExtratoRepository;
import br.com.lcano.fluxocaixa.repository.ParametroRepository;
import br.com.lcano.fluxocaixa.rsql.RsqlSpecUtil;
import br.com.lcano.fluxocaixa.utils.GzipUtil;
import br.com.lcano.fluxocaixa.utils.UsuarioUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.Date;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class ExtratoFluxoCaixaService {

    private final ImportacaoExtratoRepository importacaoExtratoRepository;
    private final ParametroRepository parametroRepository;
    private final ExtratoImportacaoAsyncService asyncService;
    private final LancamentoService lancamentoService;

    public ImportacaoExtratoDTO importarContaCorrente(MultipartFile file) {
        Long idUsuario = UsuarioUtil.getIdUsuarioAutenticado();
        validarParametro(idUsuario);
        return iniciarImportacao(file, TipoImportacaoExtrato.CONTA_CORRENTE, idUsuario, null);
    }

    public ImportacaoExtratoDTO importarFaturaCartao(MultipartFile file, Date dataVencimento) {
        Long idUsuario = UsuarioUtil.getIdUsuarioAutenticado();
        validarParametro(idUsuario);
        return iniciarImportacao(file, TipoImportacaoExtrato.FATURA_CARTAO, idUsuario, dataVencimento);
    }

    public ImportacaoExtratoDTO importarMovimentacaoB3(MultipartFile file) {
        Long idUsuario = UsuarioUtil.getIdUsuarioAutenticado();
        return iniciarImportacao(file, TipoImportacaoExtrato.MOVIMENTACAO_B3, idUsuario, null);
    }

    public Page<ImportacaoExtratoDTO> search(Pageable pageable, String filter) {
        Long idUsuario = UsuarioUtil.getIdUsuarioAutenticado();
        String idUsuarioFilter = "idUsuario==" + idUsuario;
        String fullFilter = (filter != null && !filter.isBlank())
                ? idUsuarioFilter + ";" + filter
                : idUsuarioFilter;
        Specification<ImportacaoExtrato> spec = RsqlSpecUtil.fromFilter(fullFilter);
        Pageable sorted = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("dataCriacao").descending()
        );
        return importacaoExtratoRepository.findAll(spec, sorted)
                .map(e -> new ImportacaoExtratoDTO().fromEntity(e));
    }

    public ImportacaoExtratoDTO findById(Long id) {
        Long idUsuario = UsuarioUtil.getIdUsuarioAutenticado();
        ImportacaoExtrato arquivo = importacaoExtratoRepository.findById(id)
                .orElseThrow(() -> new ExtratoException.ImportacaoNaoEncontrada(id));
        if (!arquivo.getIdUsuario().equals(idUsuario)) {
            throw new ExtratoException.ImportacaoNaoEncontrada(id);
        }
        return new ImportacaoExtratoDTO().fromEntity(arquivo);
    }

    public Page<LancamentoDTO> searchLancamentos(Long id, String filter, Pageable pageable) {
        Long idUsuario = UsuarioUtil.getIdUsuarioAutenticado();
        ImportacaoExtrato arquivo = importacaoExtratoRepository.findById(id)
                .orElseThrow(() -> new ExtratoException.ImportacaoNaoEncontrada(id));
        if (!arquivo.getIdUsuario().equals(idUsuario)) {
            throw new ExtratoException.ImportacaoNaoEncontrada(id);
        }
        String baseFilter = "importacao.id==" + id;
        String fullFilter = (filter != null && !filter.isBlank())
                ? baseFilter + ";" + filter
                : baseFilter;
        return lancamentoService.search(pageable, fullFilter);
    }

    public ImportacaoExtratoDTO findStatusById(Long id) {
        ImportacaoExtrato importacao = importacaoExtratoRepository.findById(id)
                .orElseThrow(() -> new ExtratoException.ImportacaoNaoEncontrada(id));
        return new ImportacaoExtratoDTO().fromEntity(importacao);
    }

    public ResponseEntity<byte[]> downloadArquivo(Long id) {
        Long idUsuario = UsuarioUtil.getIdUsuarioAutenticado();
        ImportacaoExtrato importacao = importacaoExtratoRepository.findById(id)
                .orElseThrow(() -> new ExtratoException.ImportacaoNaoEncontrada(id));
        if (!importacao.getIdUsuario().equals(idUsuario)) {
            throw new ExtratoException.ImportacaoNaoEncontrada(id);
        }
        if (importacao.getConteudoArquivo() == null) {
            throw new ExtratoException.ArquivoSemConteudo();
        }
        byte[] conteudo = GzipUtil.decompress(importacao.getConteudoArquivo());
        String nomeArquivo = importacao.getNomeArquivo() != null ? importacao.getNomeArquivo() : "extrato";
        MediaType contentType = nomeArquivo.toLowerCase().endsWith(".xlsx")
                ? MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                : MediaType.parseMediaType("text/csv");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nomeArquivo + "\"")
                .contentType(contentType)
                .body(conteudo);
    }

    private ImportacaoExtratoDTO iniciarImportacao(MultipartFile file, TipoImportacaoExtrato tipo,
                                                    Long idUsuario, Date dataVencimento) {
        byte[] conteudo = lerBytes(file);
        ImportacaoExtrato importacao = criarImportacao(conteudo, file.getOriginalFilename(), tipo, idUsuario);
        try {
            asyncService.processar(importacao.getId(), conteudo, dataVencimento);
        } catch (Exception e) {
            throw new LancamentoException.ErroIniciarImportacaoExtrato(e);
        }
        return new ImportacaoExtratoDTO().fromEntity(importacao);
    }

    private ImportacaoExtrato criarImportacao(byte[] conteudo, String nomeArquivo, TipoImportacaoExtrato tipo, Long idUsuario) {
        String hash = calcularHash(conteudo);
        importacaoExtratoRepository
                .findByIdUsuarioAndHashArquivoAndStatus(idUsuario, hash, StatusImportacaoExtrato.CONCLUIDO)
                .ifPresent(existente -> {
                    throw new ExtratoException.ArquivoJaImportado(existente);
                });

        ImportacaoExtrato importacao = new ImportacaoExtrato();
        importacao.setIdUsuario(idUsuario);
        importacao.setTipo(tipo);
        importacao.setStatus(StatusImportacaoExtrato.PENDENTE);
        importacao.setDataCriacao(new Date());
        importacao.setHashArquivo(hash);
        importacao.setNomeArquivo(nomeArquivo);
        importacao.setConteudoArquivo(GzipUtil.compress(conteudo));
        return importacaoExtratoRepository.save(importacao);
    }

    private void validarParametro(Long idUsuario) {
        Parametro parametro = parametroRepository.findByIdUsuario(idUsuario);
        if (parametro == null) {
            throw new ExtratoException.ParametroNaoConfigurado();
        }
    }

    private byte[] lerBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new ExtratoException.ErroLeituraArquivo(e.getMessage());
        }
    }

    private String calcularHash(byte[] conteudo) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(conteudo);
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new ExtratoException.ErroLeituraArquivo("Não foi possível calcular o hash do arquivo.");
        }
    }
}
