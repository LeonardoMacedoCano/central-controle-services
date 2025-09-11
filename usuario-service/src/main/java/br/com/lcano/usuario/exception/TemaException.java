package br.com.lcano.usuario.exception;

public abstract class TemaException extends RuntimeException {

    public TemaException(String message) {
        super(message);
    }

    public static class TemaNaoEncontrado extends TemaException {
        public TemaNaoEncontrado(Long id) {
            super("Tema não encontrado com id: " + id);
        }
    }

    public static class TemaPadraoNaoEncontrado extends TemaException {
        public TemaPadraoNaoEncontrado() {
            super("Tema padrão não encontrado para o usuário.");
        }
    }
}
