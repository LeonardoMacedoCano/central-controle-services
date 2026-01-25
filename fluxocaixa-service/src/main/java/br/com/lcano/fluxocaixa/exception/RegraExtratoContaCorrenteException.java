package br.com.lcano.fluxocaixa.exception;

public abstract class RegraExtratoContaCorrenteException extends RuntimeException {

    public RegraExtratoContaCorrenteException(String message) {
        super(message);
    }

    public static class RegraNaoEncontrado extends RegraExtratoContaCorrenteException {
        public RegraNaoEncontrado(Long id) {
            super("Regra de extrato da conta corrente não encontrado com id: " + id);
        }
    }
}