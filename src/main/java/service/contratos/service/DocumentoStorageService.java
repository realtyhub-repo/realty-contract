package service.contratos.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import service.contratos.config.R2Properties;
import service.contratos.exceptions.AlmacenamientoException;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class DocumentoStorageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final R2Properties r2;

    public void subir(String clave, byte[] contenido) {
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(r2.bucket())
                    .key(clave)
                    .contentType("application/pdf")
                    .build();
            s3Client.putObject(request, RequestBody.fromBytes(contenido));
        } catch (SdkException e) {
            throw new AlmacenamientoException("No se pudo guardar el documento del contrato", e);
        }
    }

    public String urlDescarga(String clave, String nombreArchivo, Duration vigencia) {
        try {
            GetObjectRequest objeto = GetObjectRequest.builder()
                    .bucket(r2.bucket())
                    .key(clave)
                    .responseContentType("application/pdf")
                    .responseContentDisposition("attachment; filename=\"" + nombreArchivo + "\"")
                    .build();

            GetObjectPresignRequest presign = GetObjectPresignRequest.builder()
                    .signatureDuration(vigencia)
                    .getObjectRequest(objeto)
                    .build();

            return s3Presigner.presignGetObject(presign).url().toString();
        } catch (SdkException e) {
            throw new AlmacenamientoException("No se pudo generar el enlace de descarga", e);
        }
    }
}
