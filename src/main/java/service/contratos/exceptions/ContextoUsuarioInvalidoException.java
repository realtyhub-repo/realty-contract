package service.contratos.exceptions;

public class ContextoUsuarioInvalidoException extends RuntimeException {

    public ContextoUsuarioInvalidoException(String mensaje) {
        super(mensaje);
    }
}
