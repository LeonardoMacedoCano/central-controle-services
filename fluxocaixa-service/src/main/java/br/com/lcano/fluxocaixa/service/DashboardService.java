package br.com.lcano.fluxocaixa.service;

import br.com.lcano.fluxocaixa.dto.CategoriaOperacaoResumoDTO;
import br.com.lcano.fluxocaixa.dto.CategoriaResumoDTO;
import br.com.lcano.fluxocaixa.dto.DashboardDTO;
import br.com.lcano.fluxocaixa.enums.TipoOperacaoExtratoMovimentacaoB3;
import br.com.lcano.fluxocaixa.repository.AtivoRepository;
import br.com.lcano.fluxocaixa.repository.DespesaRepository;
import br.com.lcano.fluxocaixa.repository.RendaRepository;
import br.com.lcano.fluxocaixa.utils.UsuarioUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DespesaRepository despesaRepository;
    private final RendaRepository rendaRepository;
    private final AtivoRepository ativoRepository;

    public DashboardDTO findResumo(int ano, Integer mes) {
        Long idUsuario = UsuarioUtil.getIdUsuarioAutenticado();
        Date[] periodo = calcularPeriodo(ano, mes);
        Date inicio = periodo[0];
        Date fim = periodo[1];

        BigDecimal totalReceita = rendaRepository.sumValorByPeriodo(idUsuario, inicio, fim);
        BigDecimal totalDespesa = despesaRepository.sumValorByPeriodo(idUsuario, inicio, fim);
        BigDecimal saldo = totalReceita.subtract(totalDespesa);

        BigDecimal creditoAtivos = ativoRepository.sumValorByOperacaoAndPeriodo(
                idUsuario, TipoOperacaoExtratoMovimentacaoB3.CREDITO, inicio, fim);
        BigDecimal debitoAtivos = ativoRepository.sumValorByOperacaoAndPeriodo(
                idUsuario, TipoOperacaoExtratoMovimentacaoB3.DEBITO, inicio, fim);
        BigDecimal totalInvestidoAtivos = creditoAtivos.subtract(debitoAtivos);

        List<CategoriaResumoDTO> despesasPorCategoria = despesaRepository.sumValorByCategoriaAndPeriodo(idUsuario, inicio, fim);
        List<CategoriaResumoDTO> receitasPorCategoria = rendaRepository.sumValorByCategoriaAndPeriodo(idUsuario, inicio, fim);
        List<CategoriaResumoDTO> ativosPorCategoria = toAtivoCategoriaResumo(
                ativoRepository.sumValorByCategoriaAndOperacaoAndPeriodo(idUsuario, inicio, fim));

        return new DashboardDTO(
                totalReceita, totalDespesa, saldo, totalInvestidoAtivos,
                despesasPorCategoria, receitasPorCategoria, ativosPorCategoria);
    }

    private Date[] calcularPeriodo(int ano, Integer mes) {
        LocalDate inicio = mes != null ? LocalDate.of(ano, mes, 1) : LocalDate.of(ano, 1, 1);
        LocalDate fim = mes != null ? inicio.plusMonths(1) : inicio.plusYears(1);
        ZoneId zone = ZoneId.systemDefault();
        return new Date[]{
            Date.from(inicio.atStartOfDay(zone).toInstant()),
            Date.from(fim.atStartOfDay(zone).toInstant())
        };
    }

    private List<CategoriaResumoDTO> toAtivoCategoriaResumo(List<CategoriaOperacaoResumoDTO> rows) {
        Map<String, BigDecimal> map = new LinkedHashMap<>();
        for (CategoriaOperacaoResumoDTO r : rows) {
            map.merge(r.getCategoria(),
                      r.getOperacao() == TipoOperacaoExtratoMovimentacaoB3.CREDITO ? r.getTotal() : r.getTotal().negate(),
                      BigDecimal::add);
        }
        return map.entrySet().stream()
                .map(e -> new CategoriaResumoDTO(e.getKey(), e.getValue()))
                .toList();
    }
}
