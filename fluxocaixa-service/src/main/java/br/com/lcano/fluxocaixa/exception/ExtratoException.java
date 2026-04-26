package br.com.lcano.fluxocaixa.exception;

import br.com.lcano.fluxocaixa.domain.ImportacaoExtrato;

import java.text.SimpleDateFormat;

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
        public ArquivoJaImportado(ImportacaoExtrato existente) {
            super(String.format(
                    "O arquivo '%s' já foi importado em %s — %d de %d linhas processadas.",
                    existente.getNomeArquivo(),
                    new SimpleDateFormat("dd/MM/yyyy 'às' HH:mm").format(existente.getDataConclusao()),
                    existente.getLinhasProcessadas() != null ? existente.getLinhasProcessadas() : 0,
                    existente.getTotalLinhas() != null ? existente.getTotalLinhas() : 0
            ));
        }
    }

    public static class ArquivoSemConteudo extends ExtratoException {
        public ArquivoSemConteudo() {
            super("Conteúdo do arquivo não disponível para esta importação.");
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
