package service.contratos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient usuarioRestClient(ServiciosProperties servicios) {
        return RestClient.builder()
                .baseUrl(servicios.usuariosUrl())
                .build();
    }

    @Bean
    public RestClient propiedadRestClient(ServiciosProperties servicios) {
        return RestClient.builder()
                .baseUrl(servicios.propiedadesUrl())
                .build();
    }
}
