package br.com.lcano.fluxocaixa.service;

import br.com.lcano.fluxocaixa.domain.*;
import br.com.lcano.fluxocaixa.dto.ExtratoContaCorrenteDTO;
import br.com.lcano.fluxocaixa.dto.ExtratoFaturaCartaoDTO;
import br.com.lcano.fluxocaixa.dto.ExtratoMovimentacaoB3DTO;
import br.com.lcano.fluxocaixa.enums.*;
import br.com.lcano.fluxocaixa.exception.ExtratoException;
import br.com.lcano.fluxocaixa.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExtratoLinhaProcessadorService {

    private final LancamentoRepository lancamentoRepository;
    private final DespesaRepository despesaRepository;
    private final RendaRepository rendaRepository;
    private final AtivoRepository ativoRepository;
    private final MovimentacaoCategoriaRepository movimentacaoCategoriaRepository;

    @Transactional
    public Long processarContaCorrente(ExtratoContaCorrenteDTO item,
                                       Long idUsuario,
                                       List<MapeamentoExtratoBancario> mapeamentos,
                                       Parametro parametro) {
        String descricaoOrigem = item.getDescricao();
        String descricao = item.getDescricao();
        BigDecimal valor = item.getValor();

        for (MapeamentoExtratoBancario mapeamento : mapeamentos) {
            if (mapeamento.getDescricaoMatch() == null) continue;
            if (!descricao.toLowerCase().contains(mapeamento.getDescricaoMatch().toLowerCase())) continue;

            switch (mapeamento.getTipoRegra()) {
                case IGNORAR_DESPESA -> {
                    if (valor.compareTo(BigDecimal.ZERO) < 0) return null;
                }
                case IGNORAR_RENDA -> {
                    if (valor.compareTo(BigDecimal.ZERO) > 0) return null;
                }
                case CLASSIFICAR_DESPESA -> {
                    if (valor.compareTo(BigDecimal.ZERO) < 0) {
                        MovimentacaoCategoria cat = resolverCategoria(
                                mapeamento.getDespesaCategoriaDestino(),
                                parametro != null ? parametro.getDespesaCategoriaPadrao() : null,
                                "despesa");
                        String desc = mapeamento.getDescricaoDestino() != null ? mapeamento.getDescricaoDestino() : descricao;
                        return salvarDespesa(idUsuario, item.getDataLancamento(), desc, descricaoOrigem,
                                valor.abs(), item.getDataLancamento(), cat, DespesaFormaPagamento.CARTAO_DEBITO);
                    }
                }
                case CLASSIFICAR_RENDA -> {
                    if (valor.compareTo(BigDecimal.ZERO) > 0) {
                        MovimentacaoCategoria cat = resolverCategoria(
                                mapeamento.getRendaCategoriaDestino(),
                                parametro != null ? parametro.getRendaCategoriaPadrao() : null,
                                "renda");
                        String desc = mapeamento.getDescricaoDestino() != null ? mapeamento.getDescricaoDestino() : descricao;
                        return salvarRenda(idUsuario, item.getDataLancamento(), desc, descricaoOrigem,
                                valor, item.getDataLancamento(), cat);
                    }
                }
                case CLASSIFICAR_ATIVO -> {
                    MovimentacaoCategoria cat = resolverCategoria(
                            mapeamento.getAtivoCategoriaDestino(), null, "ativo");
                    TipoOperacaoExtratoMovimentacaoB3 operacao = TipoOperacaoExtratoMovimentacaoB3.CREDITO;
                    String desc = mapeamento.getDescricaoDestino() != null ? mapeamento.getDescricaoDestino() : descricao;
                    return salvarAtivo(idUsuario, item.getDataLancamento(), desc, descricaoOrigem,
                            valor.abs(), item.getDataLancamento(), cat, operacao);
                }
            }
        }

        if (valor.compareTo(BigDecimal.ZERO) < 0) {
            MovimentacaoCategoria cat = resolverCategoria(
                    null, parametro != null ? parametro.getDespesaCategoriaPadrao() : null, "despesa");
            return salvarDespesa(idUsuario, item.getDataLancamento(), descricao, descricaoOrigem,
                    valor.abs(), item.getDataLancamento(), cat, DespesaFormaPagamento.CARTAO_DEBITO);
        } else if (valor.compareTo(BigDecimal.ZERO) > 0) {
            MovimentacaoCategoria cat = resolverCategoria(
                    null, parametro != null ? parametro.getRendaCategoriaPadrao() : null, "renda");
            return salvarRenda(idUsuario, item.getDataLancamento(), descricao, descricaoOrigem,
                    valor, item.getDataLancamento(), cat);
        }

        return null;
    }

    @Transactional
    public void processarFaturaCartao(ExtratoFaturaCartaoDTO item,
                                      Long idUsuario,
                                      Date dataVencimento,
                                      Parametro parametro,
                                      List<MapeamentoExtratoBancario> mapeamentos) {
        String descricaoOrigem = item.getDescricao();
        String descricao = item.getDescricao();
        MovimentacaoCategoria categoria = null;

        Date vencimento = dataVencimento != null ? dataVencimento : item.getDataLancamento();

        String[] parcelamento = detectarParcelamento(descricaoOrigem);
        if (parcelamento != null) {
            Optional<Despesa> heranca = buscarHerancaParcela(idUsuario, parcelamento, item.getValor(), vencimento, parametro);
            if (heranca.isPresent()) {
                descricao = heranca.get().getLancamento().getDescricao();
                categoria = heranca.get().getCategoria();
            }
        }

        if (categoria == null) {
            for (MapeamentoExtratoBancario mapeamento : mapeamentos) {
                if (mapeamento.getDescricaoMatch() == null) continue;
                if (!descricaoOrigem.toLowerCase().contains(mapeamento.getDescricaoMatch().toLowerCase())) continue;

                if (mapeamento.getTipoRegra() == TipoMapeamentoExtratoBancario.IGNORAR_DESPESA) return;
                if (mapeamento.getTipoRegra() == TipoMapeamentoExtratoBancario.CLASSIFICAR_DESPESA) {
                    if (mapeamento.getDescricaoDestino() != null) descricao = mapeamento.getDescricaoDestino();
                    if (mapeamento.getDespesaCategoriaDestino() != null) categoria = mapeamento.getDespesaCategoriaDestino();
                    break;
                }
            }
        }

        if (categoria == null) {
            if (item.getCategoria() != null) {
                categoria = findOrCreateCategoriaDespesa(capitalize(item.getCategoria()));
            } else {
                categoria = resolverCategoria(null,
                        parametro != null ? parametro.getDespesaCategoriaPadrao() : null, "despesa");
            }
        }

        salvarDespesa(idUsuario, item.getDataLancamento(), descricao, descricaoOrigem,
                item.getValor(), vencimento, categoria, DespesaFormaPagamento.CARTAO_CREDITO);
    }

    @Transactional
    public void processarMovimentacaoB3(ExtratoMovimentacaoB3DTO item,
                                        Long idUsuario,
                                        Parametro parametro) {
        String tipoMov = item.getTipoMovimentacao() != null ? item.getTipoMovimentacao().toLowerCase() : "";
        boolean isRendaPassiva = tipoMov.contains("dividendo") || tipoMov.contains("juros sobre capital");

        if (isRendaPassiva) {
            MovimentacaoCategoria cat = resolverCategoria(
                    null, parametro != null ? parametro.getRendaPassivaCategoria() : null, "renda passiva");
            salvarRenda(idUsuario, item.getDataMovimentacao(), item.getProduto(), item.getProduto(),
                    item.getPrecoTotal(), item.getDataMovimentacao(), cat);
            return;
        }

        MovimentacaoCategoria cat = resolverCategoria(
                null, parametro != null ? parametro.getCategoriaPadraoMovimentacaoB3() : null, "movimentação B3");
        TipoOperacaoExtratoMovimentacaoB3 operacao = resolverOperacaoB3(item.getTipoOperacao());
        salvarAtivo(idUsuario, item.getDataMovimentacao(), item.getProduto(), item.getProduto(),
                item.getPrecoTotal(), item.getDataMovimentacao(), cat, operacao);
    }

    @Transactional
    public int[] processarBatchContaCorrente(List<ExtratoContaCorrenteDTO> itens,
                                              Long idUsuario,
                                              List<MapeamentoExtratoBancario> mapeamentos,
                                              Parametro parametro) {
        int processadas = 0, ignoradas = 0;
        for (int i = 0; i < itens.size(); i++) {
            try {
                Long id = processarContaCorrente(itens.get(i), idUsuario, mapeamentos, parametro);
                if (id != null) processadas++; else ignoradas++;
            } catch (Exception e) {
                throw new ExtratoException.ErroNaLinha(i + 2, e.getMessage());
            }
        }
        return new int[]{processadas, ignoradas};
    }

    @Transactional
    public int[] processarBatchFaturaCartao(List<ExtratoFaturaCartaoDTO> itens,
                                             Long idUsuario,
                                             Date dataVencimento,
                                             Parametro parametro,
                                             List<MapeamentoExtratoBancario> mapeamentos) {
        int processadas = 0, ignoradas = 0;
        for (int i = 0; i < itens.size(); i++) {
            try {
                if (itens.get(i).getValor().compareTo(BigDecimal.ZERO) <= 0) {
                    ignoradas++;
                    continue;
                }
                processarFaturaCartao(itens.get(i), idUsuario, dataVencimento, parametro, mapeamentos);
                processadas++;
            } catch (Exception e) {
                throw new ExtratoException.ErroNaLinha(i + 2, e.getMessage());
            }
        }
        return new int[]{processadas, ignoradas};
    }

    @Transactional
    public int[] processarBatchMovimentacaoB3(List<ExtratoMovimentacaoB3DTO> itens,
                                               Long idUsuario,
                                               Parametro parametro) {
        int processadas = 0, ignoradas = 0;
        for (int i = 0; i < itens.size(); i++) {
            try {
                processarMovimentacaoB3(itens.get(i), idUsuario, parametro);
                processadas++;
            } catch (Exception e) {
                throw new ExtratoException.ErroNaLinha(i + 2, e.getMessage());
            }
        }
        return new int[]{processadas, ignoradas};
    }

    private Long salvarDespesa(Long idUsuario, Date dataLancamento, String descricao,
                                String descricaoOrigem, BigDecimal valor, Date dataVencimento,
                                MovimentacaoCategoria categoria,
                                DespesaFormaPagamento formaPagamento) {
        Lancamento lancamento = new Lancamento();
        lancamento.setIdUsuario(idUsuario);
        lancamento.setDataLancamento(dataLancamento);
        lancamento.setDescricao(descricao);
        lancamento.setDescricaoOrigem(descricaoOrigem);
        lancamento.setTipo(TipoLancamento.DESPESA);
        lancamento = lancamentoRepository.save(lancamento);

        Despesa despesa = new Despesa();
        despesa.setLancamento(lancamento);
        despesa.setCategoria(categoria);
        despesa.setValor(valor);
        despesa.setDataVencimento(dataVencimento);
        despesa.setFormaPagamento(formaPagamento);
        despesaRepository.save(despesa);

        return lancamento.getId();
    }

    private Long salvarRenda(Long idUsuario, Date dataLancamento, String descricao,
                              String descricaoOrigem, BigDecimal valor, Date dataRecebimento,
                              MovimentacaoCategoria categoria) {
        Lancamento lancamento = new Lancamento();
        lancamento.setIdUsuario(idUsuario);
        lancamento.setDataLancamento(dataLancamento);
        lancamento.setDescricao(descricao);
        lancamento.setDescricaoOrigem(descricaoOrigem);
        lancamento.setTipo(TipoLancamento.RENDA);
        lancamento = lancamentoRepository.save(lancamento);

        Renda renda = new Renda();
        renda.setLancamento(lancamento);
        renda.setCategoria(categoria);
        renda.setValor(valor);
        renda.setDataRecebimento(dataRecebimento);
        rendaRepository.save(renda);

        return lancamento.getId();
    }

    private Long salvarAtivo(Long idUsuario, Date dataLancamento, String descricao,
                              String descricaoOrigem, BigDecimal valor, Date dataMovimento,
                              MovimentacaoCategoria categoria,
                              TipoOperacaoExtratoMovimentacaoB3 operacao) {
        Lancamento lancamento = new Lancamento();
        lancamento.setIdUsuario(idUsuario);
        lancamento.setDataLancamento(dataLancamento);
        lancamento.setDescricao(descricao);
        lancamento.setDescricaoOrigem(descricaoOrigem);
        lancamento.setTipo(TipoLancamento.ATIVO);
        lancamento = lancamentoRepository.save(lancamento);

        Ativo ativo = new Ativo();
        ativo.setLancamento(lancamento);
        ativo.setCategoria(categoria);
        ativo.setValor(valor);
        ativo.setDataMovimento(dataMovimento);
        ativo.setOperacao(operacao);
        ativoRepository.save(ativo);

        return lancamento.getId();
    }

    private MovimentacaoCategoria resolverCategoria(MovimentacaoCategoria categoriaRegra,
                                                     MovimentacaoCategoria categoriaPadrao,
                                                     String tipo) {
        if (categoriaRegra != null) return categoriaRegra;
        if (categoriaPadrao != null) return categoriaPadrao;
        throw new IllegalStateException(
                "Nenhuma categoria de " + tipo + " configurada. Configure os parâmetros do usuário.");
    }

    private MovimentacaoCategoria findOrCreateCategoriaDespesa(String descricao) {
        return movimentacaoCategoriaRepository
                .findByDescricaoIgnoreCaseAndTipo(descricao, TipoCategoria.DESPESA)
                .orElseGet(() -> {
                    MovimentacaoCategoria nova = new MovimentacaoCategoria();
                    nova.setDescricao(descricao);
                    nova.setTipo(TipoCategoria.DESPESA);
                    return movimentacaoCategoriaRepository.save(nova);
                });
    }

    private TipoOperacaoExtratoMovimentacaoB3 resolverOperacaoB3(String tipoOperacao) {
        if (tipoOperacao == null) return TipoOperacaoExtratoMovimentacaoB3.DEBITO;
        try {
            return TipoOperacaoExtratoMovimentacaoB3.fromDescricao(tipoOperacao);
        } catch (IllegalArgumentException e) {
            return TipoOperacaoExtratoMovimentacaoB3.DEBITO;
        }
    }

    private String[] detectarParcelamento(String descricao) {
        int idx = descricao.indexOf(" - Parcela ");
        if (idx < 0) return null;
        String prefixo = descricao.substring(0, idx);
        String sufixo = descricao.substring(idx + " - Parcela ".length());
        String[] partes = sufixo.split("/");
        if (partes.length != 2) return null;
        try {
            Integer.parseInt(partes[0].trim());
            Integer.parseInt(partes[1].trim());
            return new String[]{prefixo, partes[0].trim(), partes[1].trim()};
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Optional<Despesa> buscarHerancaParcela(Long idUsuario,
                                                    String[] parcelamento,
                                                    BigDecimal valor,
                                                    Date dataVencimentoAtual,
                                                    Parametro parametro) {
        int parcelaAtual = Integer.parseInt(parcelamento[1]);
        if (parcelaAtual <= 1) return Optional.empty();

        String descOrigemAnterior = parcelamento[0]
                + " - Parcela " + (parcelaAtual - 1) + "/" + parcelamento[2];

        Calendar cal = Calendar.getInstance();
        cal.setTime(dataVencimentoAtual);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date fimMesAnterior = cal.getTime();
        cal.add(Calendar.MONTH, -1);
        Date inicioMesAnterior = cal.getTime();

        return despesaRepository
                .findParcelaAnterior(idUsuario, descOrigemAnterior, inicioMesAnterior, fimMesAnterior, valor)
                .filter(d -> foiEditadaPeloUsuario(d, parametro));
    }

    private boolean foiEditadaPeloUsuario(Despesa despesa, Parametro parametro) {
        boolean descricaoAlterada = !Objects.equals(
                despesa.getLancamento().getDescricao(), despesa.getLancamento().getDescricaoOrigem());
        boolean categoriaAlterada = parametro == null
                || parametro.getDespesaCategoriaPadrao() == null
                || !Objects.equals(despesa.getCategoria().getId(),
                                   parametro.getDespesaCategoriaPadrao().getId());
        return descricaoAlterada || categoriaAlterada;
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
