package br.com.lcano.fluxocaixa.dto;

import br.com.lcano.fluxocaixa.domain.Ativo;
import br.com.lcano.fluxocaixa.enums.TipoOperacaoExtratoMovimentacaoB3;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

@Data
public class AtivoDTO implements LancamentoItemDTO {
    private Long id;
    private MovimentacaoCategoriaDTO categoria;
    private TipoOperacaoExtratoMovimentacaoB3 operacao;
    private BigDecimal valor;
    private Date dataMovimento;

    public AtivoDTO fromEntity(Ativo entity) {
        this.id = entity.getId();
        this.operacao = entity.getOperacao();
        this.valor = entity.getValor();
        this.dataMovimento = entity.getDataMovimento();
        this.categoria = Optional.ofNullable(entity.getCategoria())
                .map(cat -> new MovimentacaoCategoriaDTO().fromEntity(cat))
                .orElse(null);
        return this;
    }

    public Ativo toEntity() {
        Ativo entity = new Ativo();
        entity.setId(this.id);
        entity.setOperacao(this.operacao);
        entity.setValor(this.valor);
        entity.setDataMovimento(this.dataMovimento);
        Optional.ofNullable(this.categoria)
                .map(MovimentacaoCategoriaDTO::toEntity)
                .ifPresent(entity::setCategoria);
        return entity;
    }
}