package br.com.lcano.usuario.config;

import br.com.lcano.usuario.dto.LoginRequestDTO;
import br.com.lcano.usuario.service.AuthorizationService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PropertiesConfig {

    private final AuthorizationService authorizationService;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    public PropertiesConfig(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @PostConstruct
    public void init() {
        registerAdminUserIfNecessary();
    }

    private void registerAdminUserIfNecessary() {
        if (!authorizationService.usuarioJaCadastrado(adminUsername)) {
            try {
                authorizationService.register(new LoginRequestDTO(adminUsername, adminPassword));
            } catch (Exception e) {
                throw new IllegalStateException("Erro ao registrar o usuário admin", e);
            }
        }
    }
}
