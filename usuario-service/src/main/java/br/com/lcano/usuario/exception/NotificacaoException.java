package br.com.lcano.usuario.exception;

public class NotificacaoException extends RuntimeException {

    public NotificacaoException(String message) {
        super(message);
    }

    public static class NotificacaoNaoEncontrada extends NotificacaoException {
        public NotificacaoNaoEncontrada(Long id) {
            super(String.format("Notificação %d não encontrada.", id));
        }
    }

    public static class SecretInvalido extends NotificacaoException {
        public SecretInvalido() {
            super("Acesso não autorizado.");
        }
    }
}
