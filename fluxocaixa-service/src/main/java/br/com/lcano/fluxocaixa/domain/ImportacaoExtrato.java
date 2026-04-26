package br.com.lcano.fluxocaixa.domain;

import br.com.lcano.fluxocaixa.enums.StatusImportacaoExtrato;
import br.com.lcano.fluxocaixa.enums.TipoImportacaoExtrato;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "arquivoextratoimportado")
public class ImportacaoExtrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "idusuario", nullable = false)
    private Long idUsuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoImportacaoExtrato tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusImportacaoExtrato status;

    @Column(name = "mensagemerro", columnDefinition = "TEXT")
    private String mensagemErro;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "datacriacao", nullable = false)
    private Date dataCriacao;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "datainicio")
    private Date dataInicio;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dataconclusao")
    private Date dataConclusao;

    @Column(name = "totallinhas")
    private Integer totalLinhas;

    @Column(name = "linhasprocessadas")
    private Integer linhasProcessadas;

    @Column(name = "linhasignoradas")
    private Integer linhasIgnoradas;

    @Column(name = "hasharquivo", nullable = false, length = 64)
    private String hashArquivo;

    @Column(name = "nomearquivo")
    private String nomeArquivo;

    @Temporal(TemporalType.DATE)
    @Column(name = "datainicioperiodo")
    private Date dataInicioPeriodo;

    @Temporal(TemporalType.DATE)
    @Column(name = "datafimperiodo")
    private Date dataFimPeriodo;

    @Column(name = "conteudoarquivo", columnDefinition = "BYTEA")
    private byte[] conteudoArquivo;
}
