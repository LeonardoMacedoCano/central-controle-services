package br.com.lcano.fluxocaixa.utils;

import org.springframework.security.core.context.SecurityContextHolder;

public class UsuarioUtil {

    public static Long getIdUsuarioAutenticado() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof String userIdString) {
            try {
                return Long.parseLong(userIdString);
            } catch (NumberFormatException e) {
                throw new IllegalStateException("ID de usuário inválido: " + userIdString);
            }
        }

        throw new IllegalStateException("Usuário não autenticado ou principal inválido.");
    }
}
