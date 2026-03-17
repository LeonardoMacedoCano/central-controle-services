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
import java.util.Date;
import java.util.List;

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
                                       List<RegraExtratoContaCorrente> regras,
                                       Parametro parametro) {
        String descricao = item.getDescricao();
        BigDecimal valor = item.getValor();

        if (descricao != null && descricao.toLowerCase().contains("transferência")) {
            return null;
        }

        for (RegraExtratoContaCorrente regra : regras) {
            if (regra.getDescricaoMatch() == null) continue;
            if (!descricao.toLowerCase().contains(regra.getDescricaoMatch().toLowerCase())) continue;

            switch (regra.getTipoRegra()) {
                case IGNORAR_DESPESA -> {
                    if (valor.compareTo(BigDecimal.ZERO) < 0) return null;
                }
                case IGNORAR_RENDA -> {
                    if (valor.compareTo(BigDecimal.ZERO) > 0) return null;
                }
                case CLASSIFICAR_DESPESA -> {
                    if (valor.compareTo(BigDecimal.ZERO) < 0) {
                        MovimentacaoCategoria cat = resolverCategoria(
                                regra.getDespesaCategoriaDestino(),
                                parametro != null ? parametro.getDespesaCategoriaPadrao() : null,
                                "despesa");
                        String desc = regra.getDescricaoDestino() != null ? regra.getDescricaoDestino() : descricao;
                        return salvarDespesa(idUsuario, item.getDataLancamento(), desc, valor.abs(),
                                item.getDataLancamento(), cat, DespesaFormaPagamento.CARTAO_DEBITO);
                    }
                }
                case CLASSIFICAR_RENDA -> {
                    if (valor.compareTo(BigDecimal.ZERO) > 0) {
                        MovimentacaoCategoria cat = resolverCategoria(
                                regra.getRendaCategoriaDestino(),
                                parametro != null ? parametro.getRendaCategoriaPadrao() : null,
                                "renda");
                        String desc = regra.getDescricaoDestino() != null ? regra.getDescricaoDestino() : descricao;
                        return salvarRenda(idUsuario, item.getDataLancamento(), desc, valor,
                                item.getDataLancamento(), cat);
                    }
                }
                case CLASSIFICAR_ATIVO -> {
                    MovimentacaoCategoria cat = resolverCategoria(
                            regra.getAtivoCategoriaDestino(), null, "ativo");
                    TipoOperacaoExtratoMovimentacaoB3 operacao = valor.compareTo(BigDecimal.ZERO) < 0
                            ? TipoOperacaoExtratoMovimentacaoB3.DEBITO
                            : TipoOperacaoExtratoMovimentacaoB3.CREDITO;
                    String desc = regra.getDescricaoDestino() != null ? regra.getDescricaoDestino() : descricao;
                    return salvarAtivo(idUsuario, item.getDataLancamento(), desc, valor.abs(),
                            item.getDataLancamento(), cat, operacao);
                }
            }
        }

        if (valor.compareTo(BigDecimal.ZERO) < 0) {
            MovimentacaoCategoria cat = resolverCategoria(
                    null, parametro != null ? parametro.getDespesaCategoriaPadrao() : null, "despesa");
            return salvarDespesa(idUsuario, item.getDataLancamento(), descricao, valor.abs(),
                    item.getDataLancamento(), cat, DespesaFormaPagamento.CARTAO_DEBITO);
        } else if (valor.compareTo(BigDecimal.ZERO) > 0) {
            MovimentacaoCategoria cat = resolverCategoria(
                    null, parametro != null ? parametro.getRendaCategoriaPadrao() : null, "renda");
            return salvarRenda(idUsuario, item.getDataLancamento(), descricao, valor,
                    item.getDataLancamento(), cat);
        }

        return null;
    }

    @Transactional
    public Long processarFaturaCartao(ExtratoFaturaCartaoDTO item,
                                      Long idUsuario,
                                      Date dataVencimento,
                                      Parametro parametro) {
        MovimentacaoCategoria categoria;
        if (item.getCategoria() != null) {
            categoria = encontrarOuCriarCategoria(capitalize(item.getCategoria()), TipoCategoria.DESPESA);
        } else {
            categoria = resolverCategoria(null,
                    parametro != null ? parametro.getDespesaCategoriaPadrao() : null, "despesa");
        }

        Date vencimento = dataVencimento != null ? dataVencimento : item.getDataLancamento();
        return salvarDespesa(idUsuario, item.getDataLancamento(), item.getDescricao(),
                item.getValor(), vencimento, categoria, DespesaFormaPagamento.CARTAO_CREDITO);
    }

    @Transactional
    public Long processarMovimentacaoB3(ExtratoMovimentacaoB3DTO item,
                                         Long idUsuario,
                                         Parametro parametro) {
        String tipoMov = item.getTipoMovimentacao() != null ? item.getTipoMovimentacao().toLowerCase() : "";
        boolean isRendaPassiva = tipoMov.contains("dividendo") || tipoMov.contains("juros sobre capital");

        if (isRendaPassiva) {
            MovimentacaoCategoria cat = resolverCategoria(
                    null, parametro != null ? parametro.getRendaPassivaCategoria() : null, "renda passiva");
            return salvarRenda(idUsuario, item.getDataMovimentacao(), item.getProduto(),
                    item.getPrecoTotal(), item.getDataMovimentacao(), cat);
        }

        String ticker = extrairTicker(item.getProduto());
        MovimentacaoCategoria cat = encontrarOuCriarCategoria(ticker, TipoCategoria.ATIVO);
        TipoOperacaoExtratoMovimentacaoB3 operacao = resolverOperacaoB3(item.getTipoOperacao());
        return salvarAtivo(idUsuario, item.getDataMovimentacao(), item.getProduto(),
                item.getPrecoTotal(), item.getDataMovimentacao(), cat, operacao);
    }

    @Transactional
    public int[] processarBatchContaCorrente(List<ExtratoContaCorrenteDTO> itens,
                                              Long idUsuario,
                                              List<RegraExtratoContaCorrente> regras,
                                              Parametro parametro) {
        int processadas = 0, ignoradas = 0;
        for (int i = 0; i < itens.size(); i++) {
            try {
                Long id = processarContaCorrente(itens.get(i), idUsuario, regras, parametro);
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
                                             Parametro parametro) {
        int processadas = 0, ignoradas = 0;
        for (int i = 0; i < itens.size(); i++) {
            try {
                if (itens.get(i).getValor().compareTo(BigDecimal.ZERO) <= 0) {
                    ignoradas++;
                    continue;
                }
                processarFaturaCartao(itens.get(i), idUsuario, dataVencimento, parametro);
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
                                BigDecimal valor, Date dataVencimento,
                                MovimentacaoCategoria categoria,
                                DespesaFormaPagamento formaPagamento) {
        Lancamento lancamento = new Lancamento();
        lancamento.setIdUsuario(idUsuario);
        lancamento.setDataLancamento(dataLancamento);
        lancamento.setDescricao(descricao);
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
                              BigDecimal valor, Date dataRecebimento,
                              MovimentacaoCategoria categoria) {
        Lancamento lancamento = new Lancamento();
        lancamento.setIdUsuario(idUsuario);
        lancamento.setDataLancamento(dataLancamento);
        lancamento.setDescricao(descricao);
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
                              BigDecimal valor, Date dataMovimento,
                              MovimentacaoCategoria categoria,
                              TipoOperacaoExtratoMovimentacaoB3 operacao) {
        Lancamento lancamento = new Lancamento();
        lancamento.setIdUsuario(idUsuario);
        lancamento.setDataLancamento(dataLancamento);
        lancamento.setDescricao(descricao);
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

    private MovimentacaoCategoria encontrarOuCriarCategoria(String descricao, TipoCategoria tipo) {
        return movimentacaoCategoriaRepository
                .findByDescricaoIgnoreCaseAndTipo(descricao, tipo)
                .orElseGet(() -> {
                    MovimentacaoCategoria nova = new MovimentacaoCategoria();
                    nova.setDescricao(descricao);
                    nova.setTipo(tipo);
                    return movimentacaoCategoriaRepository.save(nova);
                });
    }

    private String extrairTicker(String produto) {
        if (produto == null) return "OUTROS";
        return produto.trim().split("[-\\s]+")[0].toUpperCase();
    }

    private TipoOperacaoExtratoMovimentacaoB3 resolverOperacaoB3(String tipoOperacao) {
        if (tipoOperacao == null) return TipoOperacaoExtratoMovimentacaoB3.DEBITO;
        try {
            return TipoOperacaoExtratoMovimentacaoB3.fromDescricao(tipoOperacao);
        } catch (IllegalArgumentException e) {
            return TipoOperacaoExtratoMovimentacaoB3.DEBITO;
        }
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
