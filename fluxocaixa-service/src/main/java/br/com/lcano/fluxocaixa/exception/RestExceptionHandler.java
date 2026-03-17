package br.com.lcano.fluxocaixa.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;


@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String MSG_ERRO_GENERICO = "Ocorreu um erro interno no servidor: ";

    private ResponseEntity<Object> buildResponseEntity(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status).body(Map.of("error", mensagem));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleGenericException(Exception ex) {
        return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, MSG_ERRO_GENERICO + ex.getMessage());
    }

    @ExceptionHandler(RegraExtratoContaCorrenteException.RegraNaoEncontrado.class)
    protected ResponseEntity<Object> handleRegraNaoEncontrado(RegraExtratoContaCorrenteException.RegraNaoEncontrado ex) {
        return buildResponseEntity(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({LancamentoException.LancamentoNaoEncontradoById.class})
    protected ResponseEntity<Object> handleLancamentoNaoEncontradaById(LancamentoException.LancamentoNaoEncontradoById ex) {
        return buildResponseEntity(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({LancamentoException.LancamentoTipoNaoSuportado.class})
    protected ResponseEntity<Object> handleLancamentoTipoNaoSuportado(LancamentoException.LancamentoTipoNaoSuportado ex) {
        return buildResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler({LancamentoException.ErroIniciarImportacaoExtrato.class})
    protected ResponseEntity<Object> handleErroIniciarImportacaoExtrato(LancamentoException.ErroIniciarImportacaoExtrato ex) {
        return buildResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(ExtratoException.ParametroNaoConfigurado.class)
    protected ResponseEntity<Object> handleParametroNaoConfigurado(ExtratoException.ParametroNaoConfigurado ex) {
        return buildResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    @ExceptionHandler(ExtratoException.ImportacaoNaoEncontrada.class)
    protected ResponseEntity<Object> handleImportacaoNaoEncontrada(ExtratoException.ImportacaoNaoEncontrada ex) {
        return buildResponseEntity(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ExtratoException.ErroLeituraArquivo.class)
    protected ResponseEntity<Object> handleErroLeituraArquivo(ExtratoException.ErroLeituraArquivo ex) {
        return buildResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(ExtratoException.ArquivoJaImportado.class)
    protected ResponseEntity<Object> handleArquivoJaImportado(ExtratoException.ArquivoJaImportado ex) {
        return buildResponseEntity(HttpStatus.CONFLICT, ex.getMessage());
    }
}
