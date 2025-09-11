package br.com.lcano.usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDTO {
    private String username;
    private String token;
    private Long idTema;
    private String icone;
}
