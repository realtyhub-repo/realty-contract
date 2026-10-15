package service.contratos.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import service.contratos.dto.response.PropiedadContratoResponse;
import service.contratos.exceptions.PropiedadNoEncontradaException;
import service.contratos.exceptions.ServicioNoDisponibleException;

import java.util.UUID;

@Service
@Slf4j
public class PropiedadClientService {

    private final RestClient propiedadClient;

    public PropiedadClientService(@Qualifier("propiedadRestClient") RestClient propiedadClient) {
        this.propiedadClient = propiedadClient;
    }

    public PropiedadContratoResponse buscar(UUID propiedadId) {
        try {
            PropiedadContratoResponse respuesta = propiedadClient.get()
                    .uri("/internal/propiedades/{id}", propiedadId)
                    .retrieve()
                    .body(PropiedadContratoResponse.class);

            if (respuesta == null) {
                throw new ServicioNoDisponibleException("Property Service respondió sin datos");
            }
            return respuesta;

        } catch (HttpClientErrorException.NotFound e) {
            throw new PropiedadNoEncontradaException("La propiedad " + propiedadId + " no existe");
        } catch (RestClientException e) {
            log.error("No se pudo consultar la propiedad {}: {}", propiedadId, e.getMessage());
            throw new ServicioNoDisponibleException("El servicio de propiedades no está disponible");
        }
    }
}
