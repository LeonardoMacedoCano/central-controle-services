package br.com.lcano.fluxocaixa.dto;

import br.com.lcano.fluxocaixa.domain.ImportacaoExtrato;
import br.com.lcano.fluxocaixa.enums.StatusImportacaoExtrato;
import br.com.lcano.fluxocaixa.enums.TipoImportacaoExtrato;
import lombok.Data;

import java.util.Date;

@Data
public class ImportacaoExtratoDTO {

    private Long id;
    private TipoImportacaoExtrato tipo;
    private StatusImportacaoExtrato status;
    private String mensagemErro;
    private Date dataCriacao;
    private Date dataInicio;
    private Date dataConclusao;
    private Integer totalLinhas;
    private Integer linhasProcessadas;
    private Integer linhasIgnoradas;
    private Integer linhasErro;

    public ImportacaoExtratoDTO fromEntity(ImportacaoExtrato entity) {
        ImportacaoExtratoDTO dto = new ImportacaoExtratoDTO();
        dto.setId(entity.getId());
        dto.setTipo(entity.getTipo());
        dto.setStatus(entity.getStatus());
        dto.setMensagemErro(entity.getMensagemErro());
        dto.setDataCriacao(entity.getDataCriacao());
        dto.setDataInicio(entity.getDataInicio());
        dto.setDataConclusao(entity.getDataConclusao());
        dto.setTotalLinhas(entity.getTotalLinhas());
        dto.setLinhasProcessadas(entity.getLinhasProcessadas());
        dto.setLinhasIgnoradas(entity.getLinhasIgnoradas());
        dto.setLinhasErro(entity.getLinhasErro());
        return dto;
    }
}
