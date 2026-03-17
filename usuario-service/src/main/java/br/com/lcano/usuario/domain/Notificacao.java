package br.com.lcano.usuario.domain;

import br.com.lcano.usuario.enums.TipoNotificacao;
import br.com.lcano.usuario.util.BooleanToCharConverter;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "notificacao")
public class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "idusuario", nullable = false)
    private Long idUsuario;

    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(nullable = false, length = 500)
    private String mensagem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoNotificacao tipo;

    @Convert(converter = BooleanToCharConverter.class)
    @Column(nullable = false)
    private boolean lida;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "datacriacao", nullable = false)
    private Date dataCriacao;
}
