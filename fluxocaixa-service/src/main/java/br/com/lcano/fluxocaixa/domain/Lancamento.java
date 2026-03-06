package br.com.lcano.fluxocaixa.domain;

import br.com.lcano.fluxocaixa.enums.TipoLancamento;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Table(name = "lancamento")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
public class Lancamento implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "idusuario", nullable = false)
    private Long idUsuario;

    @Column(name = "datalancamento", nullable = false)
    private Date dataLancamento;

    @Column(nullable = false)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoLancamento tipo;

    @OneToOne(mappedBy = "lancamento", cascade = CascadeType.ALL)
    private Despesa despesa;

    @OneToOne(mappedBy = "lancamento", cascade = CascadeType.ALL)
    private Renda renda;

    @OneToOne(mappedBy = "lancamento", cascade = CascadeType.ALL)
    private Ativo ativo;
}