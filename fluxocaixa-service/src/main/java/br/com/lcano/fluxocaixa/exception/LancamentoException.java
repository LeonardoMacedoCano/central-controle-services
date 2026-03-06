package br.com.lcano.fluxocaixa.exception;

public class LancamentoException extends RuntimeException {
    public LancamentoException(String message) {
        super(message);
    }

    public static class LancamentoNaoEncontradoById extends LancamentoException {
        public LancamentoNaoEncontradoById(Long id) {
            super(String.format("Lançamento %d não encontrado.", id));
        }
    }

    public static class LancamentoTipoNaoSuportado extends LancamentoException {
        public LancamentoTipoNaoSuportado(String tipo) {
            super(String.format("Tipo de lançamento %s não suportado", tipo));
        }
    }

    public static class ErroIniciarImportacaoExtrato extends LancamentoException {
        public ErroIniciarImportacaoExtrato() {
            super("Não foi possível iniciar a importação do extrato. Tente novamente mais tarde.");
        }
    }
}
