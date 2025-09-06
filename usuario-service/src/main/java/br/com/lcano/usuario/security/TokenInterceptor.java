package br.com.lcano.usuario.security;

import br.com.lcano.usuario.repository.UsuarioRepository;
import br.com.lcano.usuario.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletResponse;

@Component
public class TokenInterceptor implements HandlerInterceptor {
    @Autowired
    private final TokenService tokenService;

    @Autowired
    private final UsuarioRepository usuarioRepository;

    public TokenInterceptor(TokenService tokenService, UsuarioRepository usuarioRepository) {
        this.tokenService = tokenService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public boolean preHandle(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI().substring(request.getContextPath().length());

        if (isPathExemptFromAuthentication(path)) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String usernameToken = tokenService.validateToken(token);

            if (usernameToken != null && !usernameToken.isEmpty()) {
                UserDetails usuario = usuarioRepository.findByUsername(usernameToken);
                request.setAttribute("usuario", usuario);

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