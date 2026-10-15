package service.contratos.exceptions;

public class NoReintentableException extends RuntimeException {

    public NoReintentableException(String mensaje) {
        super(mensaje);
    }
}
