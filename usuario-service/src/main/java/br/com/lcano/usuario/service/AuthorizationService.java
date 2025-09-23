package br.com.lcano.usuario.service;

import br.com.lcano.usuario.domain.Usuario;
import br.com.lcano.usuario.dto.LoginRequestDTO;
import br.com.lcano.usuario.dto.LoginResponseDTO;
import br.com.lcano.usuario.exception.UsuarioException;
import br.com.lcano.usuario.repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Date;

@AllArgsConstructor
@Service
public class AuthorizationService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final TokenService tokenService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByUsername(username);
    }

    public Usuario findUsuarioByUsername(String username) {
        return usuarioRepository.findUsuarioByUsername(username);
    }

    public boolean usuarioJaCadastrado(String username) {
        return usuarioRepository.findUsuarioByUsername(username) != null;
    }

    public boolean usuarioAtivo(String username) {
        Usuario usuario = usuarioRepository.findUsuarioByUsername(username);
        return usuario != null && usuario.isEnabled();
    }

    public LoginResponseDTO login(LoginRequestDTO data, AuthenticationManager authenticationManager) {
        if (!usuarioJaCadastrado(data.getUsername())) {
            throw new UsuarioException.CredenciaisInvalidas();
        }

        if (!usuarioAtivo(data.getUsername())) {
            throw new UsuarioException.UsuarioDesativado();
        }

        var credentials = new UsernamePasswordAuthenticationToken(data.getUsername(), data.getSenha());
        var authentication = authenticationManager.authenticate(credentials);
        Usuario usuario = (Usuario) authentication.getPrincipal();

        String token = tokenService.gerarToken(usuario);
        return mapToLoginResponseDTO(usuario, token);
    }

    public void register(LoginRequestDTO data) {
        if (usuarioJaCadastrado(data.getUsername())) {
            throw new UsuarioException.UsuarioJaCadastrado();
        }

        Usuario novoUsuario = new Usuario(
                data.getUsername(),
                new BCryptPasswordEncoder().encode(data.getSenha()),
                new Date()
        );

        usuarioRepository.save(novoUsuario);
    }

    public LoginResponseDTO validateToken(String token) {
        Long idUser = tokenService.validateToken(token);
        Usuario usuario = usuarioRepository.findById(idUser).orElseThrow(UsuarioException.UsuarioNaoEncontrado::new);
        return mapToLoginResponseDTO(usuario, token);
    }

    private LoginResponseDTO mapToLoginResponseDTO(Usuario usuario, String token) {
        String iconeBase64 = null;

        if (usuario.getIcone() != null) {
            iconeBase64 = Base64.getEncoder().encodeToString(usuario.getIcone());
        }

        return new LoginResponseDTO(
                usuario.getUsername(),
                token,
                usuario.getTema() != null ? usuario.getTema().getId() : null,
                iconeBase64
        );
    }
}
