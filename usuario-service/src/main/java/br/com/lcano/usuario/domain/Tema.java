package br.com.lcano.usuario.domain;

import br.com.lcano.usuario.util.BooleanToCharConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "tema", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"idusuario", "title"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
public class Tema implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 15)
    private String title;

    @Column(name = "primarycolor", nullable = false, length = 7)
    private String primaryColor;

    @Column(name = "secondarycolor", nullable = false, length = 7)
    private String secondaryColor;

    @Column(name = "tertiarycolor", nullable = false, length = 7)
    private String tertiaryColor;

    @Column(name = "quaternarycolor", nullable = false, length = 7)
    private String quaternaryColor;

    @Column(name = "whitecolor", nullable = false, length = 7)
    private String whiteColor;

    @Column(name = "blackcolor", nullable = false, length = 7)
    private String blackColor;

    @Column(name = "graycolor", nullable = false, length = 7)
    private String grayColor;

    @Column(name = "successcolor", nullable = false, length = 7)
    private String successColor;

    @Column(name = "infocolor", nullable = false, length = 7)
    private String infoColor;

    @Column(name = "warningcolor", nullable = false, length = 7)
    private String warningColor;

    @Convert(converter = BooleanToCharConverter.class)
    @Column(name = "isdefault", nullable = false)
    private boolean isDefault;

    @ManyToOne
    @JoinColumn(name = "idusuario")
    private Usuario usuario;
}
