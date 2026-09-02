package br.com.lcano.fluxocaixa.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Map;

@Slf4j
@Component
public class NotificacaoClient {

    private final RestTemplate restTemplate = criarRestTemplate();

    private static RestTemplate criarRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) Duration.ofSeconds(5).toMillis());
        factory.setReadTimeout((int) Duration.ofSeconds(10).toMillis());
        return new RestTemplate(factory);
    }

    @Value("${usuario.service.url:}")
    private String usuarioServiceUrl;

    @Value("${service.secret:}")
    private String serviceSecret;

    public void notificar(Long idUsuario, String titulo, String mensagem, String tipo) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Service-Secret", serviceSecret);

            Map<String, Object> body = Map.of(
                    "idUsuario", idUsuario,
                    "titulo", titulo,
                    "mensagem", mensagem,
                    "tipo", tipo
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            restTemplate.postForEntity(usuarioServiceUrl + "/api/notificacao/interna", request, Void.class);
        } catch (Exception e) {
            log.warn("Falha ao enviar notificação para usuário {}: {}", idUsuario, e.getMessage());
        }
    }
}
