package br.com.lcano.usuario.service;

import br.com.lcano.usuario.domain.Usuario;
import br.com.lcano.usuario.dto.UsuarioFormDTO;
import br.com.lcano.usuario.repository.UsuarioRepository;
import br.com.lcano.usuario.util.UsuarioUtil;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioUtil usuarioUtil;
    private final TemaService temaService;

    @Transactional
    public void updateAsDto(UsuarioFormDTO usuarioFormDTO) throws Exception {
        Usuario usuario = usuarioUtil.getUsuarioAutenticado();

        if (usuarioFormDTO.getIdTema() != null && usuarioFormDTO.getIdTema() > 0) {
            updateTema(usuario, usuarioFormDTO.getIdTema());
        }

        MultipartFile file = usuarioFormDTO.getFile();
        if (file != null && !file.isEmpty()) {
            updateArquivo(usuario, file);
        }

        usuarioRepository.save(usuario);
    }

    private void updateTema(Usuario usuario, Long idTema) {
        usuario.setTema(temaService.findById(idTema));
    }

    private void updateArquivo(Usuario usuario, MultipartFile file) throws Exception {
        usuario.setIcone(file.getBytes());
        usuarioRepository.save(usuario);
    }
}
