package service.contratos.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import service.contratos.dto.response.UsuarioContactoInternalResponse;
import service.contratos.exceptions.ServicioNoDisponibleException;
import service.contratos.exceptions.UsuarioNoEncontradoException;

import java.util.UUID;

@Service
@Slf4j
public class UsuarioClientService {

    private final RestClient usuarioClient;

    public UsuarioClientService(@Qualifier("usuarioRestClient") RestClient usuarioClient) {
        this.usuarioClient = usuarioClient;
    }

    public UsuarioContactoInternalResponse buscarContacto(UUID usuarioId) {
        try {
            UsuarioContactoInternalResponse respuesta = usuarioClient.get()
                    .uri("/internal/usuario/{id}/contacto", usuarioId)
                    .retrieve()
                    .body(UsuarioContactoInternalResponse.class);

            if (respuesta == null) {
                throw new ServicioNoDisponibleException("User Service respondió sin datos");
            }
            return respuesta;

        } catch (HttpClientErrorException.NotFound e) {
            throw new UsuarioNoEncontradoException("El usuario " + usuarioId + " no existe");
        } catch (RestClientException e) {
            log.error("No se pudo consultar el usuario {}: {}", usuarioId, e.getMessage());
            throw new ServicioNoDisponibleException("El servicio de usuarios no está disponible");
        }
    }
}
