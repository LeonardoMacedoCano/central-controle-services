package br.com.lcano.usuario.exception;

public abstract class UsuarioException extends RuntimeException {

    public UsuarioException(String message) {
        super(message);
    }

    public static class UsuarioNaoEncontrado extends UsuarioException {
        public UsuarioNaoEncontrado() {
            super("Usuário não encontrado.");
        }
    }

    public static class UsuarioDesativado extends UsuarioException {
        public UsuarioDesativado() {
            super("Usuário desativado.");
        }
    }

    public static class ErroGerarToken extends UsuarioException {
        public ErroGerarToken() {
            super("Erro ao gerar Token.");
        }
    }

    public static class TokenExpiradoOuInvalido extends UsuarioException {
        public TokenExpiradoOuInvalido() {
            super("Token expirado ou inválido.");
        }
    }

    public static class GoogleTokenInvalido extends UsuarioException {
        public GoogleTokenInvalido() {
            super("Não foi possível validar o login com o Google.");
        }
    }

    public static class EmailNaoAutorizado extends UsuarioException {
        public EmailNaoAutorizado() {
            super("E-mail não autorizado a acessar o sistema.");
        }
    }
}
