package br.com.lcano.fluxocaixa.exception;

public class MovimentacaoCategoriaException extends RuntimeException {

    public MovimentacaoCategoriaException(String message) {
        super(message);
    }

    public static class CategoriaNaoEncontrada extends MovimentacaoCategoriaException {
        public CategoriaNaoEncontrada(Long id) {
            super(String.format("Categoria %d não encontrada.", id));
        }
    }
}
