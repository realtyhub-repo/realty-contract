package service.contratos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ContratosApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContratosApplication.class, args);
    }

}
