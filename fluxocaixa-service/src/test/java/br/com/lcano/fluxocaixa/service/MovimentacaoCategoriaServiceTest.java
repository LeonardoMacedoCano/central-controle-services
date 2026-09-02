package br.com.lcano.fluxocaixa.service;

import br.com.lcano.fluxocaixa.domain.MovimentacaoCategoria;
import br.com.lcano.fluxocaixa.dto.MovimentacaoCategoriaDTO;
import br.com.lcano.fluxocaixa.enums.TipoCategoria;
import br.com.lcano.fluxocaixa.exception.MovimentacaoCategoriaException;
import br.com.lcano.fluxocaixa.repository.MovimentacaoCategoriaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MovimentacaoCategoriaServiceTest {

    private static final long USUARIO_ATUAL = 7L;

    private MovimentacaoCategoriaRepository repository;
    private MovimentacaoCategoriaService service;

    @BeforeEach
    void setUp() {
        repository = mock(MovimentacaoCategoriaRepository.class);
        service = new MovimentacaoCategoriaService(repository);
        when(repository.save(any(MovimentacaoCategoria.class))).thenAnswer(inv -> inv.getArgument(0));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(String.valueOf(USUARIO_ATUAL), null, Collections.emptyList()));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private MovimentacaoCategoria categoria(Long id, Long idUsuario) {
        MovimentacaoCategoria c = new MovimentacaoCategoria();
        c.setId(id);
        c.setIdUsuario(idUsuario);
        c.setDescricao("Mercado");
        c.setTipo(TipoCategoria.DESPESA);
        return c;
    }

    private MovimentacaoCategoriaDTO dto(Long id) {
        MovimentacaoCategoriaDTO d = new MovimentacaoCategoriaDTO();
        d.setId(id);
        d.setDescricao("Mercado");
        d.setTipo(TipoCategoria.DESPESA);
        return d;
    }

    @Test
    void saveNova_defineIdUsuarioDoAutenticado() {
        service.saveAsDto(dto(null));

        ArgumentCaptor<MovimentacaoCategoria> captor = ArgumentCaptor.forClass(MovimentacaoCategoria.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getIdUsuario()).isEqualTo(USUARIO_ATUAL);
    }

    @Test
    void saveEdicao_deCategoriaDoUsuario_atualiza() {
        MovimentacaoCategoria existente = categoria(3L, USUARIO_ATUAL);
        when(repository.findById(3L)).thenReturn(Optional.of(existente));

        MovimentacaoCategoriaDTO d = dto(3L);
        d.setDescricao("Mercado Novo");
        service.saveAsDto(d);

        assertThat(existente.getDescricao()).isEqualTo("Mercado Novo");
        verify(repository).save(existente);
    }

    @Test
    void saveEdicao_deCategoriaDeSistema_lancaNaoEncontrada() {
        when(repository.findById(3L)).thenReturn(Optional.of(categoria(3L, null)));

        assertThatThrownBy(() -> service.saveAsDto(dto(3L)))
                .isInstanceOf(MovimentacaoCategoriaException.CategoriaNaoEncontrada.class);
    }

    @Test
    void saveEdicao_deCategoriaDeOutroUsuario_lancaNaoEncontrada() {
        when(repository.findById(3L)).thenReturn(Optional.of(categoria(3L, 999L)));

        assertThatThrownBy(() -> service.saveAsDto(dto(3L)))
                .isInstanceOf(MovimentacaoCategoriaException.CategoriaNaoEncontrada.class);
    }

    @Test
    void delete_categoriaDoUsuario_deleta() {
        MovimentacaoCategoria alvo = categoria(5L, USUARIO_ATUAL);
        when(repository.findById(5L)).thenReturn(Optional.of(alvo));

        service.deleteById(5L);

        verify(repository).delete(alvo);
    }

    @Test
    void delete_categoriaDeSistema_lancaENaoDeleta() {
        when(repository.findById(5L)).thenReturn(Optional.of(categoria(5L, null)));

        assertThatThrownBy(() -> service.deleteById(5L))
                .isInstanceOf(MovimentacaoCategoriaException.CategoriaNaoEncontrada.class);
        verify(repository, never()).delete(any(MovimentacaoCategoria.class));
    }

    @Test
    void delete_categoriaDeOutroUsuario_lanca() {
        when(repository.findById(5L)).thenReturn(Optional.of(categoria(5L, 999L)));

        assertThatThrownBy(() -> service.deleteById(5L))
                .isInstanceOf(MovimentacaoCategoriaException.CategoriaNaoEncontrada.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    void findAllAsDto_aplicaSpecificationDeVisibilidadeEMapeia() {
        Page<MovimentacaoCategoria> page = new PageImpl<>(List.of(categoria(1L, null), categoria(2L, USUARIO_ATUAL)));
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Page<MovimentacaoCategoriaDTO> result = service.findAllAsDto(null, Pageable.unpaged());

        ArgumentCaptor<Specification<MovimentacaoCategoria>> spec = ArgumentCaptor.forClass(Specification.class);
        verify(repository).findAll(spec.capture(), any(Pageable.class));
        assertThat(spec.getValue()).isNotNull();
        assertThat(result.getContent()).extracting(MovimentacaoCategoriaDTO::getDescricao).containsOnly("Mercado");
    }
}
