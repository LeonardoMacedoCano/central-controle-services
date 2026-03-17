package br.com.lcano.usuario.dto;

import br.com.lcano.usuario.domain.Notificacao;
import br.com.lcano.usuario.enums.TipoNotificacao;
import lombok.Data;

import java.util.Date;

@Data
public class NotificacaoDTO {

    private Long id;
    private String titulo;
    private String mensagem;
    private TipoNotificacao tipo;
    private boolean lida;
    private Date dataCriacao;

    public NotificacaoDTO fromEntity(Notificacao entity) {
        NotificacaoDTO dto = new NotificacaoDTO();
        dto.setId(entity.getId());
        dto.setTitulo(entity.getTitulo());
        dto.setMensagem(entity.getMensagem());
        dto.setTipo(entity.getTipo());
        dto.setLida(entity.isLida());
        dto.setDataCriacao(entity.getDataCriacao());
        return dto;
    }
}
