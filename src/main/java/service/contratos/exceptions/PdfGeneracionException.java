package service.contratos.exceptions;

public class PdfGeneracionException extends RuntimeException {

    public PdfGeneracionException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
