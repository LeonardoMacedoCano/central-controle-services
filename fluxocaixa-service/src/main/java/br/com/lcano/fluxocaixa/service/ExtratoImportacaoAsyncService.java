package br.com.lcano.fluxocaixa.service;

import br.com.lcano.fluxocaixa.domain.ImportacaoExtrato;
import br.com.lcano.fluxocaixa.domain.MapeamentoExtratoBancario;
import br.com.lcano.fluxocaixa.domain.Parametro;
import br.com.lcano.fluxocaixa.dto.ExtratoImportacaoResultado;
import br.com.lcano.fluxocaixa.enums.StatusImportacaoExtrato;
import br.com.lcano.fluxocaixa.enums.TipoImportacaoExtrato;
import br.com.lcano.fluxocaixa.repository.ImportacaoExtratoRepository;
import br.com.lcano.fluxocaixa.repository.MapeamentoExtratoBancarioRepository;
import br.com.lcano.fluxocaixa.repository.ParametroRepository;
import br.com.lcano.fluxocaixa.utils.NotificacaoClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExtratoImportacaoAsyncService {

    private final ImportacaoExtratoRepository importacaoExtratoRepository;
    private final MapeamentoExtratoBancarioRepository mapeamentoExtratoBancarioRepository;
    private final ParametroRepository parametroRepository;
    private final NotificacaoClient notificacaoClient;
    private final List<ExtratoImportacaoHandler> handlerList;

    private Map<TipoImportacaoExtrato, ExtratoImportacaoHandler> handlers;

    @PostConstruct
    public void init() {
        handlers = new EnumMap<>(TipoImportacaoExtrato.class);
        for (ExtratoImportacaoHandler handler : handlerList) {
            handlers.put(handler.getTipo(), handler);
        }
    }

    @Async
    public void processar(Long importacaoId, byte[] conteudo, Date dataVencimento) {
        ImportacaoExtrato importacao = importacaoExtratoRepository.findById(importacaoId).orElse(null);
        if (importacao == null) return;

        iniciarProcessamento(importacao);

        try {
            Parametro parametro = parametroRepository.findByIdUsuario(importacao.getIdUsuario());
            List<MapeamentoExtratoBancario> mapeamentos = mapeamentoExtratoBancarioRepository
                    .findByIdUsuarioAndAtivoOrderByPrioridadeAsc(importacao.getIdUsuario(), true);

            ExtratoImportacaoHandler handler = handlers.get(importacao.getTipo());
            ExtratoImportacaoResultado resultado = handler.processar(conteudo, importacao, parametro, mapeamentos, dataVencimento);

            concluirProcessamento(importacao, resultado);
            notificarSucesso(importacao, resultado);
        } catch (Exception e) {
            log.error("Erro ao processar importação id={}", importacaoId, e);
            salvarErro(importacao, e.getMessage());
        }
    }

    private void iniciarProcessamento(ImportacaoExtrato importacao) {
        importacao.setStatus(StatusImportacaoExtrato.PROCESSANDO);
        importacao.setDataInicio(new Date());
        importacaoExtratoRepository.save(importacao);
    }

    private void concluirProcessamento(ImportacaoExtrato importacao, ExtratoImportacaoResultado resultado) {
        importacao.setStatus(StatusImportacaoExtrato.CONCLUIDO);
        importacao.setDataConclusao(new Date());
        importacao.setLinhasProcessadas(resultado.getProcessadas());
        importacao.setLinhasIgnoradas(resultado.getIgnoradas());
        importacao.setDataInicioPeriodo(resultado.getDataInicioPeriodo());
        importacao.setDataFimPeriodo(resultado.getDataFimPeriodo());
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

    private void notificarSucesso(ImportacaoExtrato importacao, ExtratoImportacaoResultado resultado) {
        String tipoLabel = switch (importacao.getTipo()) {
            case CONTA_CORRENTE -> "Conta Corrente";
            case FATURA_CARTAO -> "Fatura Cartão";
            case MOVIMENTACAO_B3 -> "Movimentação B3";
        };
        String mensagem = String.format("%s (%s): %d lançamentos importados, %d ignorados.",
                tipoLabel, importacao.getNomeArquivo(), resultado.getProcessadas(), resultado.getIgnoradas());
        notificacaoClient.notificar(importacao.getIdUsuario(), "Importação concluída", mensagem, "SUCESSO");
    }
}
