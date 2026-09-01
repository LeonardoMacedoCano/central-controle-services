package br.com.lcano.usuario.service;

import br.com.lcano.usuario.domain.Notificacao;
import br.com.lcano.usuario.dto.NotificacaoInternaDTO;
import br.com.lcano.usuario.enums.TipoNotificacao;
import br.com.lcano.usuario.exception.NotificacaoException;
import br.com.lcano.usuario.repository.NotificacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NotificacaoServiceTest {

    private static final String SECRET = "s3cr3t";

    private NotificacaoRepository repository;
    private NotificacaoService service;

    @BeforeEach
    void setUp() {
        repository = mock(NotificacaoRepository.class);
        service = new NotificacaoService(repository);
        ReflectionTestUtils.setField(service, "serviceSecret", SECRET);
    }

    private NotificacaoInternaDTO dto() {
        NotificacaoInternaDTO d = new NotificacaoInternaDTO();
        d.setIdUsuario(1L);
        d.setTitulo("t");
        d.setMensagem("m");
        d.setTipo(TipoNotificacao.SUCESSO);
        return d;
    }

    @Test
    void receiveInterna_secretInvalido_lancaENaoSalva() {
        assertThatThrownBy(() -> service.receiveInterna("errado", dto()))
                .isInstanceOf(NotificacaoException.SecretInvalido.class);
        verify(repository, never()).save(any());
    }

    @Test
    void receiveInterna_secretNulo_lanca() {
        assertThatThrownBy(() -> service.receiveInterna(null, dto()))
                .isInstanceOf(NotificacaoException.SecretInvalido.class);
        verify(repository, never()).save(any());
    }

    @Test
    void receiveInterna_secretDeTamanhoDiferente_lanca() {
        assertThatThrownBy(() -> service.receiveInterna(SECRET + "x", dto()))
                .isInstanceOf(NotificacaoException.SecretInvalido.class);
    }

    @Test
    void receiveInterna_secretValido_salvaNaoLidaComData() {
        service.receiveInterna(SECRET, dto());

        ArgumentCaptor<Notificacao> captor = ArgumentCaptor.forClass(Notificacao.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().isLida()).isFalse();
        assertThat(captor.getValue().getDataCriacao()).isNotNull();
        assertThat(captor.getValue().getIdUsuario()).isEqualTo(1L);
    }

    @Test
    void findByIdAndMarkAsLida_notificacaoDeOutroUsuario_lancaNaoEncontrada() {
        Notificacao n = new Notificacao();
        n.setId(5L);
        n.setIdUsuario(999L);
        when(repository.findById(5L)).thenReturn(Optional.of(n));

        assertThatThrownBy(() -> service.findByIdAndMarkAsLida(5L, 1L))
                .isInstanceOf(NotificacaoException.NotificacaoNaoEncontrada.class);
        verify(repository, never()).save(any());
    }

    @Test
    void markAsLida_notificacaoDeOutroUsuario_lanca() {
        Notificacao n = new Notificacao();
        n.setId(6L);
        n.setIdUsuario(999L);
        when(repository.findById(6L)).thenReturn(Optional.of(n));

        assertThatThrownBy(() -> service.markAsLida(6L, 1L, true))
                .isInstanceOf(NotificacaoException.NotificacaoNaoEncontrada.class);
    }

    @Test
    void markAsLida_dono_atualizaESalva() {
        Notificacao n = new Notificacao();
        n.setId(7L);
        n.setIdUsuario(1L);
        n.setLida(false);
        when(repository.findById(7L)).thenReturn(Optional.of(n));

        service.markAsLida(7L, 1L, true);

        assertThat(n.isLida()).isTrue();
        verify(repository).save(n);
    }
}
