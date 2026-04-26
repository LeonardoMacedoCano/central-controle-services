package br.com.lcano.fluxocaixa.service;

import br.com.lcano.fluxocaixa.domain.ImportacaoExtrato;
import br.com.lcano.fluxocaixa.domain.MapeamentoExtratoBancario;
import br.com.lcano.fluxocaixa.domain.Parametro;
import br.com.lcano.fluxocaixa.dto.ExtratoFaturaCartaoDTO;
import br.com.lcano.fluxocaixa.dto.ExtratoImportacaoResultado;
import br.com.lcano.fluxocaixa.enums.TipoImportacaoExtrato;
import br.com.lcano.fluxocaixa.repository.ImportacaoExtratoRepository;
import br.com.lcano.fluxocaixa.utils.ExtratoFaturaCartaoCSVParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FaturaCartaoImportacaoHandler implements ExtratoImportacaoHandler {

    private final ExtratoLinhaProcessadorService linhaProcessador;
    private final ImportacaoExtratoRepository importacaoExtratoRepository;

    @Override
    public TipoImportacaoExtrato getTipo() {
        return TipoImportacaoExtrato.FATURA_CARTAO;
    }

    @Override
    public ExtratoImportacaoResultado processar(byte[] conteudo, ImportacaoExtrato importacao,
                                                Parametro parametro, List<MapeamentoExtratoBancario> mapeamentos,
                                                Date dataVencimento) {
        List<ExtratoFaturaCartaoDTO> itens = ExtratoFaturaCartaoCSVParser.parse(conteudo);
        importacao.setTotalLinhas(itens.size());
        importacaoExtratoRepository.save(importacao);

        int[] resultado = linhaProcessador.processarBatchFaturaCartao(itens, importacao.getIdUsuario(), dataVencimento, parametro, mapeamentos, importacao.getId());

        Date dataInicio;
        Date dataFim;
        if (dataVencimento != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(dataVencimento);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            dataInicio = cal.getTime();
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            dataFim = cal.getTime();
        } else {
            dataInicio = itens.stream()
                    .map(ExtratoFaturaCartaoDTO::getDataLancamento)
                    .filter(Objects::nonNull)
                    .min(Comparator.naturalOrder())
                    .orElse(null);
            dataFim = itens.stream()
                    .map(ExtratoFaturaCartaoDTO::getDataLancamento)
                    .filter(Objects::nonNull)
                    .max(Comparator.naturalOrder())
                    .orElse(null);
        }

        return new ExtratoImportacaoResultado(resultado[0], resultado[1], dataInicio, dataFim);
    }
}
