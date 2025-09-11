package br.com.lcano.usuario.config;

import br.com.lcano.usuario.dto.LoginRequestDTO;
import br.com.lcano.usuario.service.AuthorizationService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PropertiesConfig {

    private final AuthorizationService authorizationService;

    @Value("${spring.jackson.time-zone}")
    private String timeZone;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${api.security.token.secret}")
    private String tokenSecret;

    @Value("${api.security.token.expiration-hours}")
    private int tokenExpirationHours;

    private static PropertiesConfig instance;

    public PropertiesConfig(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @PostConstruct
    public void init() {
        instance = this;
        registerAdminUserIfNecessary();
    }

    private void registerAdminUserIfNecessary() {
        if (!isAdminUserAlreadyRegistered()) {
            try {
                registerAdminUser();
            } catch (Exception e) {
                throw new IllegalStateException("Erro ao registrar o usuário admin", e);
            }
        }
    }

    private boolean isAdminUserAlreadyRegistered() {
        return authorizationService.usuarioJaCadastrado(adminUsername);
    }

    private void registerAdminUser() {
        var loginRequest = new LoginRequestDTO(getAdminUsername(), getAdminPassword());
        authorizationService.register(loginRequest);
    }

    public static String getTimeZone() {
        return instance.timeZone;
    }

    public static String getTokenSecret() {
        return instance.tokenSecret;
    }

    public static int getTokenExpirationHours() {
        return instance.tokenExpirationHours;
    }

    public static String getAdminUsername() {
        return instance.adminUsername;
    }

    public static String getAdminPassword() {
        return instance.adminPassword;
    }
}
