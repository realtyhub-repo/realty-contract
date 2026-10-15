package service.contratos.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "servicios")
public record ServiciosProperties(String usuariosUrl, String propiedadesUrl) {
}
