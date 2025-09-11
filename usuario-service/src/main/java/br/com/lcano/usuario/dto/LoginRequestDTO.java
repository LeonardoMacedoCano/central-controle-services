package br.com.lcano.usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {

    private Long id;
    private String username;
    private String senha;

    public LoginRequestDTO(String username, String senha) {
        this.username = username;
        this.senha = senha;
    }
}
