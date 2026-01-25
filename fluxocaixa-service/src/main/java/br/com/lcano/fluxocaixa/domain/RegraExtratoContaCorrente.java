package br.com.lcano.fluxocaixa.domain;

import br.com.lcano.fluxocaixa.enums.TipoRegraExtratoContaCorrente;
import br.com.lcano.fluxocaixa.utils.BooleanToCharConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Entity
@Data
@Table(name = "regraextratocontacorrente")
public class RegraExtratoContaCorrente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "idusuario", nullable = false)
    private Long idUsuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "tiporegra", nullable = false)
    private TipoRegraExtratoContaCorrente tipoRegra;

    @Column(nullable = false)
    private String descricao;

    @Column(name = "descricaomatch", nullable = false)
    private String descricaoMatch;

    @Column(name = "descricaodestino")
    private String descricaoDestino;

    @ManyToOne
    @JoinColumn(name = "idcategoriadespesadestino")
    private MovimentacaoCategoria despesaCategoriaDestino;

    @ManyToOne
    @JoinColumn(name = "idcategoriarendadestino")
    private MovimentacaoCategoria rendaCategoriaDestino;

    @ManyToOne
    @JoinColumn(name = "idcategoriaativodestino")
    private MovimentacaoCategoria ativoCategoriaDestino;

    @Column(nullable = false)
    private Long prioridade;

    @Convert(converter = BooleanToCharConverter.class)
    @Column(nullable = false)
    private boolean ativo;
}