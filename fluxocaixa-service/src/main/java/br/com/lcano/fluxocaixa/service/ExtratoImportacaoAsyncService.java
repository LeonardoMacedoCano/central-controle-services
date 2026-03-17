package br.com.lcano.fluxocaixa.service;

import br.com.lcano.fluxocaixa.domain.ImportacaoExtrato;
import br.com.lcano.fluxocaixa.domain.Parametro;
import br.com.lcano.fluxocaixa.domain.RegraExtratoContaCorrente;
import br.com.lcano.fluxocaixa.dto.ExtratoContaCorrenteDTO;
import br.com.lcano.fluxocaixa.dto.ExtratoFaturaCartaoDTO;
import br.com.lcano.fluxocaixa.dto.ExtratoMovimentacaoB3DTO;
import br.com.lcano.fluxocaixa.enums.StatusImportacaoExtrato;
import br.com.lcano.fluxocaixa.repository.ImportacaoExtratoRepository;
import br.com.lcano.fluxocaixa.repository.ParametroRepository;
import br.com.lcano.fluxocaixa.repository.RegraExtratoContaCorrenteRepository;
import br.com.lcano.fluxocaixa.utils.ExtratoContaCorrenteCSVParser;
import br.com.lcano.fluxocaixa.utils.ExtratoFaturaCartaoCSVParser;
import br.com.lcano.fluxocaixa.utils.ExtratoMovimentacaoB3CSVParser;
import br.com.lcano.fluxocaixa.utils.NotificacaoClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExtratoImportacaoAsyncService {

    private final ImportacaoExtratoRepository importacaoExtratoRepository;
    private final RegraExtratoContaCorrenteRepository regraExtratoContaCorrenteRepository;
    private final ParametroRepository parametroRepository;
    private final ExtratoLinhaProcessadorService linhaProcessador;
    private final NotificacaoClient notificacaoClient;

    @Async
    public void processarContaCorrente(Long importacaoId, byte[] conteudo) {
        ImportacaoExtrato importacao = importacaoExtratoRepository.findById(importacaoId).orElse(null);
        if (importacao == null) return;

        iniciarProcessamento(importacao);

        try {
            List<ExtratoContaCorrenteDTO> itens = ExtratoContaCorrenteCSVParser.parse(conteudo);
            importacao.setTotalLinhas(itens.size());
            importacaoExtratoRepository.save(importacao);

            Parametro parametro = parametroRepository.findByIdUsuario(importacao.getIdUsuario());
            List<RegraExtratoContaCorrente> regras = regraExtratoContaCorrenteRepository
                    .findByIdUsuarioAndAtivoOrderByPrioridadeAsc(importacao.getIdUsuario(), true);

            int[] resultado = linhaProcessador.processarBatchContaCorrente(itens, importacao.getIdUsuario(), regras, parametro);
            concluirProcessamento(importacao, resultado[0], resultado[1]);
            notificarSucesso(importacao, resultado[0], resultado[1]);
        } catch (Exception e) {
            log.error("Erro ao processar importação conta corrente id={}", importacaoId, e);
            salvarErro(importacao, e.getMessage());
        }
    }

    @Async
    public void processarFaturaCartao(Long importacaoId, byte[] conteudo, Date dataVencimento) {
        ImportacaoExtrato importacao = importacaoExtratoRepository.findById(importacaoId).orElse(null);
        if (importacao == null) return;

        iniciarProcessamento(importacao);

        try {
            List<ExtratoFaturaCartaoDTO> itens = ExtratoFaturaCartaoCSVParser.parse(conteudo);
            importacao.setTotalLinhas(itens.size());
            importacaoExtratoRepository.save(importacao);

            Parametro parametro = parametroRepository.findByIdUsuario(importacao.getIdUsuario());

            int[] resultado = linhaProcessador.processarBatchFaturaCartao(itens, importacao.getIdUsuario(), dataVencimento, parametro);
            concluirProcessamento(importacao, resultado[0], resultado[1]);
            notificarSucesso(importacao, resultado[0], resultado[1]);
        } catch (Exception e) {
            log.error("Erro ao processar importação fatura cartão id={}", importacaoId, e);
            salvarErro(importacao, e.getMessage());
        }
    }

    @Async
    public void processarMovimentacaoB3(Long importacaoId, byte[] conteudo) {
        ImportacaoExtrato importacao = importacaoExtratoRepository.findById(importacaoId).orElse(null);
        if (importacao == null) return;

        iniciarProcessamento(importacao);

        try {
            List<ExtratoMovimentacaoB3DTO> itens = ExtratoMovimentacaoB3CSVParser.parse(conteudo);
            importacao.setTotalLinhas(itens.size());
            importacaoExtratoRepository.save(importacao);

            Parametro parametro = parametroRepository.findByIdUsuario(importacao.getIdUsuario());

            int[] resultado = linhaProcessador.processarBatchMovimentacaoB3(itens, importacao.getIdUsuario(), parametro);
            concluirProcessamento(importacao, resultado[0], resultado[1]);
            notificarSucesso(importacao, resultado[0], resultado[1]);
        } catch (Exception e) {
            log.error("Erro ao processar importação B3 id={}", importacaoId, e);
            salvarErro(importacao, e.getMessage());
        }
    }

    private void iniciarProcessamento(ImportacaoExtrato importacao) {
        importacao.setStatus(StatusImportacaoExtrato.PROCESSANDO);
        importacao.setDataInicio(new Date());
        importacaoExtratoRepository.save(importacao);
    }

    private void concluirProcessamento(ImportacaoExtrato importacao, int processadas, int ignoradas) {
        importacao.setStatus(StatusImportacaoExtrato.CONCLUIDO);
        importacao.setDataConclusao(new Date());
        importacao.setLinhasProcessadas(processadas);
        importacao.setLinhasIgnoradas(ignoradas);
        importacao.setLinhasErro(0);
        importacaoExtratoRepository.save(importacao);
    }

    private void salvarErro(ImportacaoExtrato importacao, String detalhe) {
        String mensagem = String.format("Arquivo '%s' — %s", importacao.getNomeArquivo(), detalhe);
        importacao.setStatus(StatusImportacaoExtrato.ERRO);
        importacao.setDataConclusao(new Date());
        importacao.setMensagemErro(mensagem);
        importacao.setHashArquivo(null);
        importacaoExtratoRepository.save(importacao);
        notificacaoClient.notificar(importacao.getIdUsuario(), "Importação falhou", mensagem, "ERRO");
    }

    private void notificarSucesso(ImportacaoExtrato importacao, int processadas, int ignoradas) {
        String tipoLabel = switch (importacao.getTipo()) {
            case CONTA_CORRENTE -> "Conta Corrente";
            case FATURA_CARTAO -> "Fatura Cartão";
            case MOVIMENTACAO_B3 -> "Movimentação B3";
        };
        String mensagem = String.format("%s (%s): %d lançamentos importados, %d ignorados.",
                tipoLabel, importacao.getNomeArquivo(), processadas, ignoradas);
        notificacaoClient.notificar(importacao.getIdUsuario(), "Importação concluída", mensagem, "SUCESSO");
    }
}
