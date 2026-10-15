package service.contratos.exceptions;

public class DatosContratoInvalidosException extends NoReintentableException {

    public DatosContratoInvalidosException(String mensaje) {
        super(mensaje);
    }
}
