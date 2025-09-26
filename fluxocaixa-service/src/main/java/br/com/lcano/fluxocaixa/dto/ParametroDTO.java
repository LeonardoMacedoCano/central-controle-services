package br.com.lcano.fluxocaixa.dto;

import br.com.lcano.fluxocaixa.domain.Parametro;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ParametroDTO {
    private Long id;
    private MovimentacaoCategoriaDTO despesaCategoriaPadrao;
    private BigDecimal metaLimiteDespesaMensal;
    private MovimentacaoCategoriaDTO rendaCategoriaPadrao;
    private MovimentacaoCategoriaDTO rendaPassivaCategoria;
    private BigDecimal metaAporteMensal;
    private BigDecimal metaAporteTotal;
    private Long diaPadraoVencimentoCartao;

    public ParametroDTO fromEntity(Parametro entity) {
        if (null == entity) return null;

        this.id = entity.getId();
        this.metaLimiteDespesaMensal = entity.getMetaLimiteDespesaMensal();
        this.metaAporteMensal = entity.getMetaAporteMensal();
        this.metaAporteTotal = entity.getMetaAporteTotal();
        this.diaPadraoVencimentoCartao = entity.getDiaPadraoVencimentoCartao();

        if (entity.getDespesaCategoriaPadrao() != null) {
            this.despesaCategoriaPadrao = new MovimentacaoCategoriaDTO().fromEntity(
                    entity.getDespesaCategoriaPadrao()
            );
        }

        if (entity.getRendaCategoriaPadrao() != null) {
            this.rendaCategoriaPadrao = new MovimentacaoCategoriaDTO().fromEntity(
                    entity.getRendaCategoriaPadrao()
            );
        }

        if (entity.getRendaPassivaCategoria() != null) {
            this.rendaPassivaCategoria = new MovimentacaoCategoriaDTO().fromEntity(
                    entity.getRendaPassivaCategoria()
            );
        }

        return this;
    }

    public Parametro toEntity() {
        Parametro entity = new Parametro();

        entity.setId(this.id);
        entity.setMetaLimiteDespesaMensal(this.metaLimiteDespesaMensal);
        entity.setMetaAporteMensal(this.metaAporteMensal);
        entity.setMetaAporteTotal(this.metaAporteTotal);
        entity.setDiaPadraoVencimentoCartao(this.diaPadraoVencimentoCartao);

        if (this.despesaCategoriaPadrao != null) {
            entity.setDespesaCategoriaPadrao(this.despesaCategoriaPadrao.toEntity());
        }

        if (this.rendaCategoriaPadrao != null) {
            entity.setRendaCategoriaPadrao(this.rendaCategoriaPadrao.toEntity());
        }

        if (this.rendaPassivaCategoria != null) {
            entity.setRendaPassivaCategoria(this.rendaPassivaCategoria.toEntity());
        }

        return entity;
    }
}
