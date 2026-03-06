package br.com.lcano.fluxocaixa.dto;

import br.com.lcano.fluxocaixa.domain.Despesa;
import br.com.lcano.fluxocaixa.enums.DespesaFormaPagamento;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

@Data
public class DespesaDTO implements LancamentoItemDTO {
    private Long id;
    private MovimentacaoCategoriaDTO categoria;
    private Date dataVencimento;
    private BigDecimal valor;
    private DespesaFormaPagamento formaPagamento;

    public DespesaDTO fromEntity(Despesa entity) {
        this.id = entity.getId();
        this.dataVencimento = entity.getDataVencimento();
        this.valor = entity.getValor();
        this.formaPagamento = entity.getFormaPagamento();
        this.categoria = Optional.ofNullable(entity.getCategoria())
                .map(cat -> new MovimentacaoCategoriaDTO().fromEntity(cat))
                .orElse(null);
        return this;
    }

    public Despesa toEntity() {
        Despesa entity = new Despesa();
        entity.setId(this.id);
        entity.setDataVencimento(this.dataVencimento);
        entity.setValor(this.valor);
        entity.setFormaPagamento(this.formaPagamento);
        Optional.ofNullable(this.categoria)
                .map(MovimentacaoCategoriaDTO::toEntity)
                .ifPresent(entity::setCategoria);
        return entity;
    }
}
