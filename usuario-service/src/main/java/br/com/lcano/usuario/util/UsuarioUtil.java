package br.com.lcano.usuario.util;

import br.com.lcano.usuario.domain.Usuario;
import br.com.lcano.usuario.service.AuthorizationService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class UsuarioUtil {

    private final AuthorizationService authorizationService;

    public Usuario getUsuarioAutenticado() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            return authorizationService.findUsuarioByUsername(userDetails.getUsername());
        } else if (principal instanceof String username) {
            return authorizationService.findUsuarioByUsername(username);
        } else {
            throw new IllegalStateException(
                    "Tipo de Principal não suportado: " + principal.getClass().getName()
            );
        }
    }
}
