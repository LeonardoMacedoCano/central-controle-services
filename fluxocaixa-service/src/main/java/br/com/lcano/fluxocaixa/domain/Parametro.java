package br.com.lcano.fluxocaixa.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "parametro", uniqueConstraints = {
        @UniqueConstraint(columnNames = "idusuario", name = "ukparametrousuario")
})
public class Parametro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "idusuario", nullable = false)
    private Long idUsuario;

    @ManyToOne
    @JoinColumn(name = "idcategoriadespesapadrao")
    private MovimentacaoCategoria despesaCategoriaPadrao;

    @Column(name = "metalimitedespesamensal")
    private BigDecimal metaLimiteDespesaMensal;

    @ManyToOne
    @JoinColumn(name = "idcategoriarendapadrao")
    private MovimentacaoCategoria rendaCategoriaPadrao;

    @ManyToOne
    @JoinColumn(name = "idcategoriarendapassiva")
    private MovimentacaoCategoria rendaPassivaCategoria;

    @Column(name = "metaaportemensal")
    private BigDecimal metaAporteMensal;

    @Column(name = "metaaportetotal", precision = 15, scale = 2)
    private BigDecimal metaAporteTotal;

    @Column(name = "diapadraovencimentocartao", nullable = false)
    private Long diaPadraoVencimentoCartao;
}
