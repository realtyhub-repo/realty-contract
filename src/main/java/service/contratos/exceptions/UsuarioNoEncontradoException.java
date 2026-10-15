package service.contratos.exceptions;

public class UsuarioNoEncontradoException extends NoReintentableException {

    public UsuarioNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
