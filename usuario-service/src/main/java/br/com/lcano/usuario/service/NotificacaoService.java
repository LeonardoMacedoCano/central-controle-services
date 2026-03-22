package br.com.lcano.usuario.service;

import br.com.lcano.usuario.domain.Notificacao;
import br.com.lcano.usuario.dto.NotificacaoDTO;
import br.com.lcano.usuario.dto.NotificacaoInternaDTO;
import br.com.lcano.usuario.exception.NotificacaoException;
import br.com.lcano.usuario.repository.NotificacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class NotificacaoService {

    private final NotificacaoRepository repository;

    @Value("${service.secret}")
    private String serviceSecret;

    public void receiveInterna(String secret, NotificacaoInternaDTO dto) {
        if (!serviceSecret.equals(secret)) {
            throw new NotificacaoException.SecretInvalido();
        }
        Notificacao notificacao = new Notificacao();
        notificacao.setIdUsuario(dto.getIdUsuario());
        notificacao.setTitulo(dto.getTitulo());
        notificacao.setMensagem(dto.getMensagem());
        notificacao.setTipo(dto.getTipo());
        notificacao.setLida(false);
        notificacao.setDataCriacao(new Date());
        repository.save(notificacao);
    }

    public NotificacaoDTO findByIdAndMarkAsLida(Long id, Long idUsuario) {
        Notificacao notificacao = repository.findById(id)
                .orElseThrow(() -> new NotificacaoException.NotificacaoNaoEncontrada(id));
        if (!notificacao.getIdUsuario().equals(idUsuario)) {
            throw new NotificacaoException.NotificacaoNaoEncontrada(id);
        }

        notificacao.setLida(true);
        repository.save(notificacao);

        return new NotificacaoDTO().fromEntity(notificacao);
    }

    public Page<NotificacaoDTO> findByUsuario(Long idUsuario, boolean apenasNaoLidas, Pageable pageable) {
        Page<Notificacao> page = apenasNaoLidas
                ? repository.findByIdUsuarioAndLidaOrderByDataCriacaoDesc(idUsuario, false, pageable)
                : repository.findByIdUsuarioOrderByDataCriacaoDesc(idUsuario, pageable);
        return page.map(n -> new NotificacaoDTO().fromEntity(n));
    }

    public long countNaoLidas(Long idUsuario) {
        return repository.countByIdUsuarioAndLida(idUsuario, false);
    }

    public void markAsLida(Long id, Long idUsuario, boolean lida) {
        Notificacao notificacao = repository.findById(id)
                .orElseThrow(() -> new NotificacaoException.NotificacaoNaoEncontrada(id));
        if (!notificacao.getIdUsuario().equals(idUsuario)) {
            throw new NotificacaoException.NotificacaoNaoEncontrada(id);
        }
        notificacao.setLida(lida);
        repository.save(notificacao);
    }

    public void markTodasAsLida(Long idUsuario) {
        var notificacoes = repository.findByIdUsuarioAndLida(idUsuario, false);
        notificacoes.forEach(n -> n.setLida(true));
        repository.saveAll(notificacoes);
    }
}
