package br.com.lcano.fluxocaixa.service;

import br.com.lcano.fluxocaixa.domain.ImportacaoExtrato;
import br.com.lcano.fluxocaixa.domain.MapeamentoExtratoBancario;
import br.com.lcano.fluxocaixa.domain.Parametro;
import br.com.lcano.fluxocaixa.dto.ExtratoImportacaoResultado;
import br.com.lcano.fluxocaixa.dto.ExtratoMovimentacaoB3DTO;
import br.com.lcano.fluxocaixa.enums.TipoImportacaoExtrato;
import br.com.lcano.fluxocaixa.repository.ImportacaoExtratoRepository;
import br.com.lcano.fluxocaixa.utils.ExtratoMovimentacaoB3XLSXParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class MovimentacaoB3ImportacaoHandler implements ExtratoImportacaoHandler {

    private final ExtratoLinhaProcessadorService linhaProcessador;
    private final ImportacaoExtratoRepository importacaoExtratoRepository;

    @Override
    public TipoImportacaoExtrato getTipo() {
        return TipoImportacaoExtrato.MOVIMENTACAO_B3;
    }

    @Override
    public ExtratoImportacaoResultado processar(byte[] conteudo, ImportacaoExtrato importacao,
                                                Parametro parametro, List<MapeamentoExtratoBancario> mapeamentos,
                                                Date dataVencimento) {
        List<ExtratoMovimentacaoB3DTO> itens = ExtratoMovimentacaoB3XLSXParser.parse(conteudo);
        importacao.setTotalLinhas(itens.size());
        importacaoExtratoRepository.save(importacao);

        int[] resultado = linhaProcessador.processarBatchMovimentacaoB3(itens, importacao.getIdUsuario(), parametro, importacao.getId());

        Date dataInicio = itens.stream()
                .map(ExtratoMovimentacaoB3DTO::getDataMovimentacao)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(null);
        Date dataFim = itens.stream()
                .map(ExtratoMovimentacaoB3DTO::getDataMovimentacao)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null);

        return new ExtratoImportacaoResultado(resultado[0], resultado[1], dataInicio, dataFim);
    }
}
