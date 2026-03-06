package br.com.lcano.fluxocaixa.domain;

import br.com.lcano.fluxocaixa.enums.DespesaFormaPagamento;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import lombok.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "despesa")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
public class Despesa implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "idlancamento", nullable = false)
    private Lancamento lancamento;

    @ManyToOne
    @JoinColumn(name = "idcategoria", nullable = false)
    private MovimentacaoCategoria categoria;

    @Column(name = "datavencimento", nullable = false)
    private Date dataVencimento;

    @Column(nullable = false)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(name = "formapagamento", nullable = false)
    private DespesaFormaPagamento formaPagamento;
}