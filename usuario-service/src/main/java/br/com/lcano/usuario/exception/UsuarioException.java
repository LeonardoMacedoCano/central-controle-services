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

    public static class UsuarioJaCadastrado extends UsuarioException {
        public UsuarioJaCadastrado() {
            super("Usuário já cadastrado.");
        }
    }

    public static class CredenciaisInvalidas extends UsuarioException {
        public CredenciaisInvalidas() {
            super("Credenciais inválidas.");
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

    public static class SenhaAtualIncorreta extends UsuarioException {
        public SenhaAtualIncorreta() {
            super("A senha atual fornecida está incorreta. Verifique e tente novamente.");
        }
    }
}
