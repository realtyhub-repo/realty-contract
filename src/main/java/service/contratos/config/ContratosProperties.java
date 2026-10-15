package service.contratos.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "contratos")
public record ContratosProperties(String paisPorDefecto, int urlDescargaMinutos) {
}
