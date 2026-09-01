package br.com.lcano.fluxocaixa.service;

import br.com.lcano.fluxocaixa.domain.Lancamento;
import br.com.lcano.fluxocaixa.dto.DespesaDTO;
import br.com.lcano.fluxocaixa.enums.TipoLancamento;
import br.com.lcano.fluxocaixa.exception.LancamentoException;
import br.com.lcano.fluxocaixa.repository.LancamentoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class LancamentoServiceTest {

    private static final long USUARIO_ATUAL = 7L;
    private static final long OUTRO_USUARIO = 99L;

    private LancamentoRepository repository;
    private LancamentoItemService despesaItemService;
    private LancamentoService service;

    @BeforeEach
    void setUp() {
        repository = mock(LancamentoRepository.class);
        despesaItemService = mock(LancamentoItemService.class);
        when(despesaItemService.getTipo()).thenReturn(TipoLancamento.DESPESA);

        service = new LancamentoService(repository, List.of(despesaItemService));
        service.init();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(String.valueOf(USUARIO_ATUAL), null, Collections.emptyList()));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private Lancamento lancamentoDe(long id, long idUsuario) {
        Lancamento l = new Lancamento();
        l.setId(id);
        l.setIdUsuario(idUsuario);
        l.setTipo(TipoLancamento.DESPESA);
        return l;
    }

    @Test
    void findByIdAsDto_lancamentoDeOutroUsuario_lancaNaoEncontrado() {
        when(repository.findById(10L)).thenReturn(Optional.of(lancamentoDe(10L, OUTRO_USUARIO)));

        assertThatThrownBy(() -> service.findByIdAsDto(10L))
                .isInstanceOf(LancamentoException.LancamentoNaoEncontradoById.class);
        verify(despesaItemService, never()).findByLancamentoId(any());
    }

    @Test
    void findByIdAsDto_lancamentoDoProprioUsuario_retornaDto() {
        when(repository.findById(11L)).thenReturn(Optional.of(lancamentoDe(11L, USUARIO_ATUAL)));
        when(despesaItemService.findByLancamentoId(11L)).thenReturn(new DespesaDTO());

        var dto = service.findByIdAsDto(11L);

        assertThat(dto.getId()).isEqualTo(11L);
        assertThat(dto.getTipo()).isEqualTo(TipoLancamento.DESPESA);
    }

    @Test
    void deleteById_lancamentoDeOutroUsuario_lancaEnaoDeleta() {
        when(repository.findById(20L)).thenReturn(Optional.of(lancamentoDe(20L, OUTRO_USUARIO)));

        assertThatThrownBy(() -> service.deleteById(20L))
                .isInstanceOf(LancamentoException.LancamentoNaoEncontradoById.class);
        verify(repository, never()).delete(any(Lancamento.class));
        verify(repository, never()).deleteById(anyLong());
    }

    @Test
    void deleteById_lancamentoInexistente_lanca() {
        when(repository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteById(404L))
                .isInstanceOf(LancamentoException.LancamentoNaoEncontradoById.class);
    }

    @Test
    void deleteById_lancamentoDoProprioUsuario_deleta() {
        Lancamento alvo = lancamentoDe(21L, USUARIO_ATUAL);
        when(repository.findById(21L)).thenReturn(Optional.of(alvo));

        service.deleteById(21L);

        verify(repository).delete(alvo);
    }
}
