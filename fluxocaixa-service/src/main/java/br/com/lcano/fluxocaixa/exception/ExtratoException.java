package br.com.lcano.fluxocaixa.exception;

public class ExtratoException extends RuntimeException {

    public ExtratoException(String message) {
        super(message);
    }

    public static class ParametroNaoConfigurado extends ExtratoException {
        public ParametroNaoConfigurado() {
            super("Parâmetros do usuário não configurados. Configure os parâmetros antes de importar.");
        }
    }

    public static class ImportacaoNaoEncontrada extends ExtratoException {
        public ImportacaoNaoEncontrada(Long id) {
            super(String.format("Importação %d não encontrada.", id));
        }
    }

    public static class ErroLeituraArquivo extends ExtratoException {
        public ErroLeituraArquivo(String detalhe) {
            super("Erro ao ler arquivo de extrato: " + detalhe);
        }
    }

    public static class ArquivoJaImportado extends ExtratoException {
        public ArquivoJaImportado(String nomeArquivo) {
            super(String.format("O arquivo '%s' já foi importado anteriormente.", nomeArquivo));
        }
    }

    public static class ErroNaLinha extends ExtratoException {
        private final int linha;

        public ErroNaLinha(int linha, String detalhe) {
            super(String.format("Erro na linha %d: %s", linha, detalhe));
            this.linha = linha;
        }

        public int getLinha() {
            return linha;
        }
    }
}
