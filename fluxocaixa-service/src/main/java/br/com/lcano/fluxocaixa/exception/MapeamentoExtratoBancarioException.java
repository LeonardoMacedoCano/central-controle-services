package br.com.lcano.fluxocaixa.exception;

public abstract class MapeamentoExtratoBancarioException extends RuntimeException {

    public MapeamentoExtratoBancarioException(String message) {
        super(message);
    }

    public static class MapeamentoNaoEncontrado extends MapeamentoExtratoBancarioException {
        public MapeamentoNaoEncontrado(Long id) {
            super("Mapeamento de extrato bancário não encontrado com id: " + id);
        }
    }
}
