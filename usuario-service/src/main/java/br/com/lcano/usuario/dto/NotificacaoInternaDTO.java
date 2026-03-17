package br.com.lcano.usuario.dto;

import br.com.lcano.usuario.enums.TipoNotificacao;
import lombok.Data;

@Data
public class NotificacaoInternaDTO {
    private Long idUsuario;
    private String titulo;
    private String mensagem;
    private TipoNotificacao tipo;
}
