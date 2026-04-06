package br.com.lcano.fluxocaixa.service;

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
import java.util.Calendar;
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

        List<CategoriaResumoDTO> despesasPorCategoria = toCategoriaResumo(
                despesaRepository.sumValorByCategoriaAndPeriodo(idUsuario, inicio, fim));
        List<CategoriaResumoDTO> receitasPorCategoria = toCategoriaResumo(
                rendaRepository.sumValorByCategoriaAndPeriodo(idUsuario, inicio, fim));
        List<CategoriaResumoDTO> ativosPorCategoria = toAtivoCategoriaResumo(
                ativoRepository.sumValorByCategoriaAndOperacaoAndPeriodo(idUsuario, inicio, fim));

        return new DashboardDTO(
                totalReceita, totalDespesa, saldo, totalInvestidoAtivos,
                despesasPorCategoria, receitasPorCategoria, ativosPorCategoria);
    }

    private Date[] calcularPeriodo(int ano, Integer mes) {
        Calendar inicio = Calendar.getInstance();
        inicio.set(ano, mes != null ? mes - 1 : Calendar.JANUARY, 1, 0, 0, 0);
        inicio.set(Calendar.MILLISECOND, 0);

        Calendar fim = (Calendar) inicio.clone();
        if (mes != null) {
            fim.add(Calendar.MONTH, 1);
        } else {
            fim.set(Calendar.YEAR, ano + 1);
        }

        return new Date[]{inicio.getTime(), fim.getTime()};
    }

    private List<CategoriaResumoDTO> toCategoriaResumo(List<Object[]> rows) {
        return rows.stream()
                .map(r -> new CategoriaResumoDTO((String) r[0], (BigDecimal) r[1]))
                .toList();
    }

    private List<CategoriaResumoDTO> toAtivoCategoriaResumo(List<Object[]> rows) {
        Map<String, BigDecimal> map = new LinkedHashMap<>();
        for (Object[] r : rows) {
            String cat = (String) r[0];
            TipoOperacaoExtratoMovimentacaoB3 op = (TipoOperacaoExtratoMovimentacaoB3) r[1];
            BigDecimal val = (BigDecimal) r[2];
            map.merge(cat, op == TipoOperacaoExtratoMovimentacaoB3.CREDITO ? val : val.negate(), BigDecimal::add);
        }
        return map.entrySet().stream()
                .map(e -> new CategoriaResumoDTO(e.getKey(), e.getValue()))
                .toList();
    }
}
