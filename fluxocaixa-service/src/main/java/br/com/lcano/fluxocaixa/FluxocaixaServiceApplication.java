package br.com.lcano.fluxocaixa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class FluxocaixaServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FluxocaixaServiceApplication.class, args);
    }

}
