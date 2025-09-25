package br.com.lcano.fluxocaixa.dto;

import br.com.lcano.fluxocaixa.domain.MovimentacaoCategoria;
import br.com.lcano.fluxocaixa.enums.TipoCategoria;
import lombok.Data;

@Data
public class MovimentacaoCategoriaDTO {
    private Long id;
    private String descricao;
    private TipoCategoria tipo;

    public MovimentacaoCategoriaDTO fromEntity(MovimentacaoCategoria entity) {
        this.id = entity.getId();
        this.descricao = entity.getDescricao();
        this.tipo = entity.getTipo();
        return this;
    }

    public MovimentacaoCategoria toEntity() {
        MovimentacaoCategoria entity = new MovimentacaoCategoria();
        entity.setId(this.id);
        entity.setDescricao(this.descricao);
        entity.setTipo(this.tipo);
        return entity;
    }
}
