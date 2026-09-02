package br.com.lcano.fluxocaixa.service;

import br.com.lcano.fluxocaixa.dto.DashboardDTO;
import br.com.lcano.fluxocaixa.enums.TipoOperacaoExtratoMovimentacaoB3;
import br.com.lcano.fluxocaixa.repository.AtivoRepository;
import br.com.lcano.fluxocaixa.repository.DespesaRepository;
import br.com.lcano.fluxocaixa.repository.RendaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DashboardServiceTest {

    private DespesaRepository despesaRepository;
    private RendaRepository rendaRepository;
    private AtivoRepository ativoRepository;
    private DashboardService service;

    @BeforeEach
    void setUp() {
        despesaRepository = mock(DespesaRepository.class);
        rendaRepository = mock(RendaRepository.class);
        ativoRepository = mock(AtivoRepository.class);
        service = new DashboardService(despesaRepository, rendaRepository, ativoRepository);
        ReflectionTestUtils.setField(service, "timeZone", "GMT");

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("7", null, Collections.emptyList()));

        when(rendaRepository.sumValorByPeriodo(anyLong(), any(), any())).thenReturn(new BigDecimal("1000.00"));
        when(despesaRepository.sumValorByPeriodo(anyLong(), any(), any())).thenReturn(new BigDecimal("300.00"));
        when(ativoRepository.sumValorByOperacaoAndPeriodo(anyLong(), eq(TipoOperacaoExtratoMovimentacaoB3.CREDITO), any(), any()))
                .thenReturn(new BigDecimal("500.00"));
        when(ativoRepository.sumValorByOperacaoAndPeriodo(anyLong(), eq(TipoOperacaoExtratoMovimentacaoB3.DEBITO), any(), any()))
                .thenReturn(new BigDecimal("200.00"));
        when(despesaRepository.sumValorByCategoriaAndPeriodo(anyLong(), any(), any())).thenReturn(List.of());
        when(rendaRepository.sumValorByCategoriaAndPeriodo(anyLong(), any(), any())).thenReturn(List.of());
        when(ativoRepository.sumValorByCategoriaAndOperacaoAndPeriodo(anyLong(), any(), any())).thenReturn(List.of());
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void saldoEhReceitaMenosDespesaEInvestidoEhCreditoMenosDebito() {
        DashboardDTO dto = service.findResumo(2026, 3);

        assertThat(dto.getTotalReceita()).isEqualByComparingTo("1000.00");
        assertThat(dto.getTotalDespesa()).isEqualByComparingTo("300.00");
        assertThat(dto.getSaldo()).isEqualByComparingTo("700.00");
        assertThat(dto.getTotalInvestidoAtivos()).isEqualByComparingTo("300.00");
    }

    @Test
    void periodoMensalUsaPrimeiroDiaDoMesAtePrimeiroDiaDoMesSeguinteEmGmt() {
        service.findResumo(2026, 3);

        ArgumentCaptor<Date> inicio = ArgumentCaptor.forClass(Date.class);
        ArgumentCaptor<Date> fim = ArgumentCaptor.forClass(Date.class);
        org.mockito.Mockito.verify(rendaRepository).sumValorByPeriodo(anyLong(), inicio.capture(), fim.capture());

        assertThat(inicio.getValue().toInstant()).isEqualTo(Instant.parse("2026-03-01T00:00:00Z"));
        assertThat(fim.getValue().toInstant()).isEqualTo(Instant.parse("2026-04-01T00:00:00Z"));
    }

    @Test
    void periodoAnualQuandoMesNulo() {
        service.findResumo(2026, null);

        ArgumentCaptor<Date> inicio = ArgumentCaptor.forClass(Date.class);
        ArgumentCaptor<Date> fim = ArgumentCaptor.forClass(Date.class);
        org.mockito.Mockito.verify(rendaRepository).sumValorByPeriodo(anyLong(), inicio.capture(), fim.capture());

        assertThat(inicio.getValue().toInstant()).isEqualTo(Instant.parse("2026-01-01T00:00:00Z"));
        assertThat(fim.getValue().toInstant()).isEqualTo(Instant.parse("2027-01-01T00:00:00Z"));
    }
}
