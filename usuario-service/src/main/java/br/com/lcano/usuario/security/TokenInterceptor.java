package br.com.lcano.usuario.security;

import br.com.lcano.usuario.repository.UsuarioRepository;
import br.com.lcano.usuario.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@AllArgsConstructor
public class TokenInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI().substring(request.getContextPath().length());

        if (isPathExemptFromAuthentication(path)) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            Long userId = tokenService.validateToken(token);

            if (userId != null) {
                request.setAttribute("userId", userId);
                return true;
            }
        }

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido");
        return false;
    }


    private boolean isPathExemptFromAuthentication(String path) {
        return (path.startsWith("/api/auth") && (path.length() == 9 || path.charAt(9) == '/')) ||
                path.equals("/api/tema/default");
    }
}
