package br.com.lcano.fluxocaixa.rsql;

import br.com.lcano.fluxocaixa.enums.TipoLancamento;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Garante que o RSQL converte o argumento textual para o tipo Java do campo
 * antes de montar o predicado. Sem isso, filtros por data (campo java.util.Date,
 * usado por Lancamento.dataLancamento) chegavam ao banco como String -> 500.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
class RsqlSpecificationConvertValueTest {

    private Object capturarValorDoEqual(String property, Class<?> javaType, String rawValue) {
        Root root = mock(Root.class);
        Path path = mock(Path.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);

        doReturn(path).when(root).get(property);
        doReturn(javaType).when(path).getJavaType();

        RsqlSearchCriteria criteria =
                new RsqlSearchCriteria(property, RsqlSearchOperation.EQUAL, List.of(rawValue));
        new RsqlSpecification<>(criteria).toPredicate(root, query, cb);

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(cb).equal(any(), captor.capture());
        return captor.getValue();
    }

    @Test
    void campoLongConverteParaLong() {
        assertThat(capturarValorDoEqual("idUsuario", Long.class, "5")).isEqualTo(5L);
    }

    @Test
    void campoDateConverteParaTimestamp() {
        Object valor = capturarValorDoEqual("dataLancamento", Date.class, "2026-01-15");
        assertThat(valor).isInstanceOf(Timestamp.class);
        assertThat(valor).isEqualTo(Timestamp.valueOf("2026-01-15 00:00:00"));
    }

    @Test
    void campoEnumConverteParaEnum() {
        assertThat(capturarValorDoEqual("tipo", TipoLancamento.class, "DESPESA"))
                .isEqualTo(TipoLancamento.DESPESA);
    }

    @Test
    void campoBooleanConverteParaBoolean() {
        assertThat(capturarValorDoEqual("ativo", Boolean.class, "true")).isEqualTo(Boolean.TRUE);
    }

    @Test
    void campoStringPermaneceString() {
        assertThat(capturarValorDoEqual("descricao", String.class, "mercado")).isEqualTo("mercado");
    }
}
