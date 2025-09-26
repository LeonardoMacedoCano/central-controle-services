package br.com.lcano.fluxocaixa.service;

import br.com.lcano.fluxocaixa.domain.Parametro;
import br.com.lcano.fluxocaixa.dto.ParametroDTO;
import br.com.lcano.fluxocaixa.repository.ParametroRepository;
import br.com.lcano.fluxocaixa.utils.UsuarioUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ParametroService {
    @Autowired
    private final ParametroRepository repository;

    public Parametro findByUsuario() {
        return repository.findByIdUsuario(
                UsuarioUtil.getIdUsuarioAutenticado()
        );
    }

    public Long saveAsDto(ParametroDTO dto) {
        Parametro parametro = dto.toEntity();
        parametro.setIdUsuario(UsuarioUtil.getIdUsuarioAutenticado());
        Parametro parametroSaved = repository.save(parametro);
        return parametroSaved.getId();
    }
}
