package service.contratos.exceptions;

public class ContratoNoEncontradoException extends RuntimeException {

    public ContratoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
