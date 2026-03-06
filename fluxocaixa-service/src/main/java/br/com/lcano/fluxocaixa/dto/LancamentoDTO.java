package br.com.lcano.fluxocaixa.dto;

import br.com.lcano.fluxocaixa.domain.Lancamento;
import br.com.lcano.fluxocaixa.enums.TipoLancamento;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

import java.util.Date;

@Data
public class LancamentoDTO {
    private Long id;
    private Date dataLancamento;
    private String descricao;
    private TipoLancamento tipo;

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "tipo")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = DespesaDTO.class, name = "DESPESA"),
            @JsonSubTypes.Type(value = RendaDTO.class, name = "RENDA"),
            @JsonSubTypes.Type(value = AtivoDTO.class, name = "ATIVO"),
    })
    private LancamentoItemDTO itemDTO;

    public LancamentoDTO fromEntityWithItem(Lancamento entity, LancamentoItemDTO itemDTO) {
        this.fromEntity(entity);
        this.itemDTO = itemDTO;
        return this;
    }

    public LancamentoDTO fromEntity(Lancamento entity) {
        this.id = entity.getId();
        this.dataLancamento = entity.getDataLancamento();
        this.descricao = entity.getDescricao();
        this.tipo = entity.getTipo();
        return this;
    }

    public Lancamento toEntity() {
        Lancamento entity = new Lancamento();
        entity.setId(this.id);
        entity.setDataLancamento(this.dataLancamento);
        entity.setDescricao(this.descricao);
        entity.setTipo(this.tipo);
        return entity;
    }
}