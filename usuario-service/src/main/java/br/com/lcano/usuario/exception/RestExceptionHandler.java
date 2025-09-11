package br.com.lcano.usuario.exception;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
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

    @ExceptionHandler({BadCredentialsException.class, JWTVerificationException.class, UsuarioException.CredenciaisInvalidas.class})
    protected ResponseEntity<Object> handleInvalidCredentials(Exception ex) {
        return buildResponseEntity(HttpStatus.UNAUTHORIZED, "Credenciais inválidas.");
    }

    @ExceptionHandler(UsuarioException.UsuarioJaCadastrado.class)
    protected ResponseEntity<Object> handleUsuarioJaCadastrado(UsuarioException.UsuarioJaCadastrado ex) {
        return buildResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(UsuarioException.UsuarioNaoEncontrado.class)
    protected ResponseEntity<Object> handleUsuarioNaoEncontrado(UsuarioException.UsuarioNaoEncontrado ex) {
        return buildResponseEntity(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({UsuarioException.UsuarioDesativado.class, UsuarioException.TokenExpiradoOuInvalido.class})
    protected ResponseEntity<Object> handleUnauthorized(UsuarioException ex) {
        return buildResponseEntity(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(UsuarioException.ErroGerarToken.class)
    protected ResponseEntity<Object> handleErroGerarToken(UsuarioException.ErroGerarToken ex) {
        return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(UsuarioException.SenhaAtualIncorreta.class)
    protected ResponseEntity<Object> handleSenhaAtualIncorreta(UsuarioException.SenhaAtualIncorreta ex) {
        return buildResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(TemaException.TemaNaoEncontrado.class)
    protected ResponseEntity<Object> handleTemaNaoEncontrado(TemaException.TemaNaoEncontrado ex) {
        return buildResponseEntity(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(TemaException.TemaPadraoNaoEncontrado.class)
    protected ResponseEntity<Object> handleTemaPadraoNaoEncontrado(TemaException.TemaPadraoNaoEncontrado ex) {
        return buildResponseEntity(HttpStatus.NOT_FOUND, ex.getMessage());
    }

}
