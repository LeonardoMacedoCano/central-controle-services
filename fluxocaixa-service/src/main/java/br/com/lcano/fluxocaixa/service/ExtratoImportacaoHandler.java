package br.com.lcano.fluxocaixa.service;

import br.com.lcano.fluxocaixa.domain.ImportacaoExtrato;
import br.com.lcano.fluxocaixa.domain.MapeamentoExtratoBancario;
import br.com.lcano.fluxocaixa.domain.Parametro;
import br.com.lcano.fluxocaixa.dto.ExtratoImportacaoResultado;
import br.com.lcano.fluxocaixa.enums.TipoImportacaoExtrato;

import java.util.Date;
import java.util.List;

public interface ExtratoImportacaoHandler {

    TipoImportacaoExtrato getTipo();

    ExtratoImportacaoResultado processar(
            byte[] conteudo,
            ImportacaoExtrato importacao,
            Parametro parametro,
            List<MapeamentoExtratoBancario> mapeamentos,
            Date dataVencimento
    );
}
