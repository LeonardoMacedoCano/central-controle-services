package br.com.lcano.fluxocaixa.dto;

import br.com.lcano.fluxocaixa.domain.Renda;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

@Data
public class RendaDTO implements LancamentoItemDTO {
    private Long id;
    private MovimentacaoCategoriaDTO categoria;
    private BigDecimal valor;
    private Date dataRecebimento;

    public RendaDTO fromEntity(Renda entity) {
        this.id = entity.getId();
        this.dataRecebimento = entity.getDataRecebimento();
        this.valor = entity.getValor();
        this.categoria = Optional.ofNullable(entity.getCategoria())
                .map(cat -> new MovimentacaoCategoriaDTO().fromEntity(cat))
                .orElse(null);
        return this;
    }

    public Renda toEntity() {
        Renda entity = new Renda();
        entity.setId(this.id);
        entity.setDataRecebimento(this.dataRecebimento);
        entity.setValor(this.valor);
        Optional.ofNullable(this.categoria)
                .map(MovimentacaoCategoriaDTO::toEntity)
                .ifPresent(entity::setCategoria);
        return entity;
    }
}