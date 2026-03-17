package br.com.lcano.fluxocaixa.service;

import br.com.lcano.fluxocaixa.domain.ImportacaoExtrato;
import br.com.lcano.fluxocaixa.domain.Parametro;
import br.com.lcano.fluxocaixa.dto.ImportacaoExtratoDTO;
import br.com.lcano.fluxocaixa.enums.StatusImportacaoExtrato;
import br.com.lcano.fluxocaixa.enums.TipoImportacaoExtrato;
import br.com.lcano.fluxocaixa.exception.ExtratoException;
import br.com.lcano.fluxocaixa.exception.LancamentoException;
import br.com.lcano.fluxocaixa.repository.ImportacaoExtratoRepository;
import br.com.lcano.fluxocaixa.repository.ParametroRepository;
import br.com.lcano.fluxocaixa.utils.UsuarioUtil;
import lombok.RequiredArgsConstructor;
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

    public ImportacaoExtratoDTO importarContaCorrente(MultipartFile file) {
        Long idUsuario = UsuarioUtil.getIdUsuarioAutenticado();
        validarParametro(idUsuario);
        byte[] conteudo = lerBytes(file);
        ImportacaoExtrato importacao = criarImportacao(conteudo, file.getOriginalFilename(), TipoImportacaoExtrato.CONTA_CORRENTE, idUsuario);
        try {
            asyncService.processarContaCorrente(importacao.getId(), conteudo);
        } catch (Exception e) {
            throw new LancamentoException.ErroIniciarImportacaoExtrato(e);
        }
        return new ImportacaoExtratoDTO().fromEntity(importacao);
    }

    public ImportacaoExtratoDTO importarFaturaCartao(MultipartFile file, Date dataVencimento) {
        Long idUsuario = UsuarioUtil.getIdUsuarioAutenticado();
        validarParametro(idUsuario);
        byte[] conteudo = lerBytes(file);
        ImportacaoExtrato importacao = criarImportacao(conteudo, file.getOriginalFilename(), TipoImportacaoExtrato.FATURA_CARTAO, idUsuario);
        try {
            asyncService.processarFaturaCartao(importacao.getId(), conteudo, dataVencimento);
        } catch (Exception e) {
            throw new LancamentoException.ErroIniciarImportacaoExtrato(e);
        }
        return new ImportacaoExtratoDTO().fromEntity(importacao);
    }

    public ImportacaoExtratoDTO importarMovimentacaoB3(MultipartFile file) {
        Long idUsuario = UsuarioUtil.getIdUsuarioAutenticado();
        byte[] conteudo = lerBytes(file);
        ImportacaoExtrato importacao = criarImportacao(conteudo, file.getOriginalFilename(), TipoImportacaoExtrato.MOVIMENTACAO_B3, idUsuario);
        try {
            asyncService.processarMovimentacaoB3(importacao.getId(), conteudo);
        } catch (Exception e) {
            throw new LancamentoException.ErroIniciarImportacaoExtrato(e);
        }
        return new ImportacaoExtratoDTO().fromEntity(importacao);
    }

    public ImportacaoExtratoDTO consultarStatus(Long id) {
        ImportacaoExtrato importacao = importacaoExtratoRepository.findById(id)
                .orElseThrow(() -> new ExtratoException.ImportacaoNaoEncontrada(id));
        return new ImportacaoExtratoDTO().fromEntity(importacao);
    }

    private byte[] lerBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new ExtratoException.ErroLeituraArquivo(e.getMessage());
        }
    }

    private ImportacaoExtrato criarImportacao(byte[] conteudo, String nomeArquivo, TipoImportacaoExtrato tipo, Long idUsuario) {
        String hash = calcularHash(conteudo);
        importacaoExtratoRepository
                .findByIdUsuarioAndHashArquivoAndStatus(idUsuario, hash, StatusImportacaoExtrato.CONCLUIDO)
                .ifPresent(existente -> {
                    throw new ExtratoException.ArquivoJaImportado(nomeArquivo);
                });

        ImportacaoExtrato importacao = new ImportacaoExtrato();
        importacao.setIdUsuario(idUsuario);
        importacao.setTipo(tipo);
        importacao.setStatus(StatusImportacaoExtrato.PENDENTE);
        importacao.setDataCriacao(new Date());
        importacao.setHashArquivo(hash);
        importacao.setNomeArquivo(nomeArquivo);
        return importacaoExtratoRepository.save(importacao);
    }

    private void validarParametro(Long idUsuario) {
        Parametro parametro = parametroRepository.findByIdUsuario(idUsuario);
        if (parametro == null) {
            throw new ExtratoException.ParametroNaoConfigurado();
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
