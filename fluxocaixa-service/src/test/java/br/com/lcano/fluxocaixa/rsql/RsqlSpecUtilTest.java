package br.com.lcano.fluxocaixa.rsql;

import br.com.lcano.fluxocaixa.domain.Lancamento;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RsqlSpecUtilTest {

    @Test
    void filtroNuloOuVazioRetornaNull() {
        assertThat(RsqlSpecUtil.<Lancamento>fromFilter(null)).isNull();
        assertThat(RsqlSpecUtil.<Lancamento>fromFilter("")).isNull();
        assertThat(RsqlSpecUtil.<Lancamento>fromFilter("   ")).isNull();
    }

    @Test
    void filtroSimplesRetornaSpecification() {
        Specification<Lancamento> spec = RsqlSpecUtil.fromFilter("idUsuario==5");
        assertThat(spec).isNotNull();
    }

    @Test
    void filtroCompostoComAndRetornaSpecification() {
        Specification<Lancamento> spec = RsqlSpecUtil.fromFilter("idUsuario==5;tipo==DESPESA");
        assertThat(spec).isNotNull();
    }

    @Test
    void operadorDesconhecidoLancaExcecao() {
        assertThatThrownBy(() -> RsqlSpecUtil.fromFilter("idUsuario=xx=5"))
                .isInstanceOf(RuntimeException.class);
    }
}
