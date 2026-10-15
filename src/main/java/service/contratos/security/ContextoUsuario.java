package service.contratos.security;

import service.contratos.enums.RolUsuario;

import java.util.UUID;

public record ContextoUsuario(UUID userId, RolUsuario rol) {
}
