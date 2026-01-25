package br.com.lcano.fluxocaixa.dto;

import br.com.lcano.fluxocaixa.domain.RegraExtratoContaCorrente;
import br.com.lcano.fluxocaixa.enums.TipoRegraExtratoContaCorrente;
import lombok.Data;

@Data
public class RegraExtratoContaCorrenteDTO {
    private Long id;
    private TipoRegraExtratoContaCorrente tipoRegra;
    private String descricao;
    private String descricaoMatch;
    private String descricaoDestino;
    private MovimentacaoCategoriaDTO despesaCategoriaDestino;
    private MovimentacaoCategoriaDTO rendaCategoriaDestino;
    private MovimentacaoCategoriaDTO ativoCategoriaDestino;
    private Long prioridade;
    private boolean ativo;

    public RegraExtratoContaCorrenteDTO fromEntity(RegraExtratoContaCorrente entity) {
        this.id = entity.getId();
        this.tipoRegra = entity.getTipoRegra();
        this.descricao = entity.getDescricao();
        this.descricaoMatch = entity.getDescricaoMatch();
        this.descricaoDestino = entity.getDescricaoDestino();
        this.despesaCategoriaDestino = entity.getDespesaCategoriaDestino() != null
                ? new MovimentacaoCategoriaDTO().fromEntity(entity.getDespesaCategoriaDestino())
                : null;
        this.rendaCategoriaDestino = entity.getRendaCategoriaDestino() != null
                ? new MovimentacaoCategoriaDTO().fromEntity(entity.getRendaCategoriaDestino())
                : null;
        this.ativoCategoriaDestino = entity.getAtivoCategoriaDestino() != null
                ? new MovimentacaoCategoriaDTO().fromEntity(entity.getAtivoCategoriaDestino())
                : null;
        this.prioridade = entity.getPrioridade();
        this.ativo = entity.isAtivo();
        return this;
    }

    public RegraExtratoContaCorrente toEntity() {
        RegraExtratoContaCorrente entity = new RegraExtratoContaCorrente();
        entity.setId(this.id);
        entity.setTipoRegra(this.tipoRegra);
        entity.setDescricao(this.descricao);
        entity.setDescricaoMatch(this.descricaoMatch);
        entity.setDescricaoDestino(this.descricaoDestino);
        if (this.despesaCategoriaDestino != null) entity.setDespesaCategoriaDestino(this.despesaCategoriaDestino.toEntity());
        if (this.rendaCategoriaDestino != null) entity.setRendaCategoriaDestino(this.rendaCategoriaDestino.toEntity());
        if (this.ativoCategoriaDestino != null) entity.setAtivoCategoriaDestino(this.ativoCategoriaDestino.toEntity());
        entity.setPrioridade(this.prioridade);
        entity.setAtivo(this.ativo);
        return entity;
    }
}
