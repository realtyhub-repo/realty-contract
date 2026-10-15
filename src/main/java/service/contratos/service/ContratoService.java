package service.contratos.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.contratos.config.ContratosProperties;
import service.contratos.dto.event.LeadOfertaAceptadaEvent;
import service.contratos.dto.response.ContratoDetalleResponse;
import service.contratos.dto.response.ContratoResponse;
import service.contratos.dto.response.DocumentoDescargaResponse;
import service.contratos.dto.response.PropiedadContratoResponse;
import service.contratos.dto.response.UsuarioContactoInternalResponse;
import service.contratos.entity.Contrato;
import service.contratos.entity.Documento;
import service.contratos.entity.PlantillaContrato;
import service.contratos.enums.EstadoComercial;
import service.contratos.enums.EstadoContrato;
import service.contratos.enums.Modalidad;
import service.contratos.enums.RolUsuario;
import service.contratos.enums.TipoContrato;
import service.contratos.exceptions.AccesoNoAutorizadoException;
import service.contratos.exceptions.ContratoNoEditableException;
import service.contratos.exceptions.ContratoNoEncontradoException;
import service.contratos.exceptions.DatosContratoInvalidosException;
import service.contratos.exceptions.DocumentoNoDisponibleException;
import service.contratos.exceptions.PlantillaNoEncontradaException;
import service.contratos.exceptions.PrecioRequeridoException;
import service.contratos.exceptions.TransicionInvalidaException;
import service.contratos.repository.ContratoRepository;
import service.contratos.repository.ContratoSpecifications;
import service.contratos.repository.DocumentoRepository;
import service.contratos.repository.PlantillaContratoRepository;
import service.contratos.security.ContextoUsuario;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContratoService {

    private static final ZoneId BOGOTA = ZoneId.of("America/Bogota");

    private final ContratoRepository contratoRepository;
    private final PlantillaContratoRepository plantillaRepository;
    private final DocumentoRepository documentoRepository;
    private final PropiedadClientService propiedadClientService;
    private final UsuarioClientService usuarioClientService;
    private final ContratoRenderer renderer;
    private final ContratoPdfGenerator pdfGenerator;
    private final DocumentoStorageService storageService;
    private final ContratoEventPublisher eventPublisher;
    private final ContratosProperties propiedades;

    public void crearBorradorDesdeLead(LeadOfertaAceptadaEvent evento) {
        if (contratoRepository.existsByLeadId(evento.leadId())) {
            log.info("Ya existe un contrato para el lead {}, se ignora el evento", evento.leadId());
            return;
        }

        PropiedadContratoResponse propiedad = propiedadClientService.buscar(evento.propiedadId());
        validarPropiedad(propiedad);

        TipoContrato tipo = tipoSegunModalidad(propiedad.modalidad());
        String pais = propiedades.paisPorDefecto();

        PlantillaContrato plantilla = plantillaRepository.findByActivaIsTrueAndTipoAndPais(tipo, pais)
                .orElseThrow(() -> new PlantillaNoEncontradaException(
                        "No hay plantilla activa de tipo " + tipo + " para el país " + pais));

        UsuarioContactoInternalResponse cliente = usuarioClientService.buscarContacto(evento.clienteId());
        UsuarioContactoInternalResponse agente = usuarioClientService.buscarContacto(evento.agenteId());

        Contrato contrato = Contrato.builder()
                .leadId(evento.leadId())
                .propiedadId(evento.propiedadId())
                .clienteId(evento.clienteId())
                .agenteId(evento.agenteId())
                .plantillaId(plantilla.getId())
                .tipo(tipo)
                .estado(EstadoContrato.BORRADOR)
                .pais(pais)
                .precioAcordado(precioValido(propiedad.precio()))
                .nombreCliente(textoSeguro(cliente.nombre()))
                .nombreAgente(textoSeguro(agente.nombre()))
                .direccionPropiedad(textoSeguro(propiedad.direccion()))
                .ciudadPropiedad(textoSeguro(propiedad.ciudad()))
                .contenido("")
                .build();

        contrato.setContenido(renderer.render(plantilla.getCuerpo(), contrato));

        try {
            contratoRepository.saveAndFlush(contrato);
            log.info("Borrador de contrato creado para el lead {}", evento.leadId());
        } catch (DataIntegrityViolationException e) {
            log.info("El contrato del lead {} ya fue creado por otra entrega del evento", evento.leadId());
        }
    }

    @Transactional(readOnly = true)
    public ContratoDetalleResponse obtenerDetalle(UUID id, ContextoUsuario contexto) {
        Contrato contrato = buscarContrato(id);
        validarParticipante(contrato, contexto);
        return ContratoDetalleResponse.from(contrato);
    }

    @Transactional(readOnly = true)
    public List<ContratoResponse> listarMisContratos(ContextoUsuario contexto, EstadoContrato estado) {
        Specification<Contrato> filtro = switch (contexto.rol()) {
            case CLIENTE -> ContratoSpecifications.porCliente(contexto.userId());
            case AGENTE -> ContratoSpecifications.porAgente(contexto.userId());
            default -> throw new AccesoNoAutorizadoException("Tu rol no tiene un listado de contratos propio");
        };

        if (estado != null) {
            filtro = filtro.and(ContratoSpecifications.conEstado(estado));
        }

        return contratoRepository.findAll(filtro, Sort.by(Sort.Direction.DESC, "createdAt")).stream()
                .map(ContratoResponse::from)
                .toList();
    }

    @Transactional
    public ContratoResponse actualizar(UUID id, BigDecimal precioAcordado, ContextoUsuario contexto) {
        Contrato contrato = buscarContrato(id);
        validarAgenteOAdmin(contrato, contexto);

        if (contrato.getEstado() != EstadoContrato.BORRADOR) {
            throw new ContratoNoEditableException("Solo un contrato en BORRADOR puede modificarse");
        }

        contrato.setPrecioAcordado(precioAcordado);
        contrato.setContenido(renderer.render(buscarPlantilla(contrato).getCuerpo(), contrato));

        return ContratoResponse.from(contratoRepository.save(contrato));
    }

    @Transactional
    public ContratoResponse firmar(UUID id, ContextoUsuario contexto) {
        Contrato contrato = buscarContrato(id);
        validarAgenteOAdmin(contrato, contexto);

        if (contrato.getEstado() != EstadoContrato.BORRADOR) {
            throw new TransicionInvalidaException("Solo un contrato en BORRADOR puede firmarse");
        }
        if (contrato.getPrecioAcordado() == null) {
            throw new PrecioRequeridoException("Define el precio acordado antes de firmar");
        }

        contrato.setEstado(EstadoContrato.FIRMADO);
        contrato.setFirmadoEn(LocalDateTime.now(BOGOTA));
        contrato.setContenido(renderer.render(buscarPlantilla(contrato).getCuerpo(), contrato));

        byte[] pdf = pdfGenerator.generar(contrato);
        int version = documentoRepository.findFirstByContratoIdOrderByVersionDesc(contrato.getId())
                .map(d -> d.getVersion() + 1)
                .orElse(1);
        String clave = "contratos/" + contrato.getId() + "/v" + version + ".pdf";

        storageService.subir(clave, pdf);

        documentoRepository.save(Documento.builder()
                .contratoId(contrato.getId())
                .version(version)
                .claveArchivo(clave)
                .hashSha256(sha256(pdf))
                .tamanoBytes((long) pdf.length)
                .build());

        Contrato guardado = contratoRepository.save(contrato);
        eventPublisher.publicarFirmado(guardado);

        return ContratoResponse.from(guardado);
    }

    @Transactional
    public ContratoResponse cancelar(UUID id, ContextoUsuario contexto) {
        Contrato contrato = buscarContrato(id);
        validarAgenteOAdmin(contrato, contexto);

        if (contrato.getEstado() == EstadoContrato.CANCELADO) {
            throw new TransicionInvalidaException("El contrato ya está cancelado");
        }

        boolean estabaFirmado = contrato.getEstado() == EstadoContrato.FIRMADO;
        contrato.setEstado(EstadoContrato.CANCELADO);
        Contrato guardado = contratoRepository.save(contrato);

        if (estabaFirmado) {
            eventPublisher.publicarCancelado(guardado);
        }

        return ContratoResponse.from(guardado);
    }

    @Transactional(readOnly = true)
    public byte[] generarVistaPrevia(UUID id, ContextoUsuario contexto) {
        Contrato contrato = buscarContrato(id);
        validarParticipante(contrato, contexto);

        if (contrato.getEstado() != EstadoContrato.BORRADOR) {
            throw new DocumentoNoDisponibleException(
                    "La vista previa solo existe para contratos en BORRADOR");
        }

        return pdfGenerator.generar(contrato);
    }

    @Transactional(readOnly = true)
    public DocumentoDescargaResponse obtenerDocumento(UUID id, ContextoUsuario contexto) {
        Contrato contrato = buscarContrato(id);
        validarParticipante(contrato, contexto);

        Documento documento = documentoRepository.findFirstByContratoIdOrderByVersionDesc(contrato.getId())
                .orElseThrow(() -> new DocumentoNoDisponibleException("El contrato aún no tiene un documento firmado"));

        Duration vigencia = Duration.ofMinutes(propiedades.urlDescargaMinutos());
        String url = storageService.urlDescarga(
                documento.getClaveArchivo(), "contrato-" + contrato.getId() + ".pdf", vigencia);

        return new DocumentoDescargaResponse(
                url,
                documento.getVersion(),
                documento.getHashSha256(),
                LocalDateTime.now(BOGOTA).plus(vigencia));
    }

    private Contrato buscarContrato(UUID id) {
        return contratoRepository.findById(id)
                .orElseThrow(() -> new ContratoNoEncontradoException("Contrato no encontrado"));
    }

    private PlantillaContrato buscarPlantilla(Contrato contrato) {
        return plantillaRepository.findById(contrato.getPlantillaId())
                .orElseThrow(() -> new PlantillaNoEncontradaException(
                        "La plantilla del contrato ya no existe"));
    }

    private void validarAgenteOAdmin(Contrato contrato, ContextoUsuario contexto) {
        boolean esAgenteDelContrato = contrato.getAgenteId().equals(contexto.userId());
        boolean esAdmin = contexto.rol() == RolUsuario.ADMINISTRADOR_CENTRAL;

        if (!esAgenteDelContrato && !esAdmin) {
            throw new AccesoNoAutorizadoException("No tienes permiso sobre este contrato");
        }
    }

    private void validarParticipante(Contrato contrato, ContextoUsuario contexto) {
        boolean esAgenteDelContrato = contrato.getAgenteId().equals(contexto.userId());
        boolean esClienteDelContrato = contrato.getClienteId().equals(contexto.userId());
        boolean esAdmin = contexto.rol() == RolUsuario.ADMINISTRADOR_CENTRAL;

        if (!esAgenteDelContrato && !esClienteDelContrato && !esAdmin) {
            throw new AccesoNoAutorizadoException("No tienes permiso sobre este contrato");
        }
    }

    private void validarPropiedad(PropiedadContratoResponse propiedad) {
        EstadoComercial estado = propiedad.estadoComercial();
        if (estado == EstadoComercial.VENDIDO || estado == EstadoComercial.ALQUILADO) {
            throw new DatosContratoInvalidosException(
                    "La propiedad " + propiedad.id() + " ya está " + estado);
        }
    }

    private TipoContrato tipoSegunModalidad(Modalidad modalidad) {
        if (modalidad == null) {
            throw new DatosContratoInvalidosException("La propiedad no informa su modalidad");
        }
        return switch (modalidad) {
            case VENTA -> TipoContrato.COMPRAVENTA;
            case ALQUILER -> TipoContrato.ALQUILER;
        };
    }

    private BigDecimal precioValido(BigDecimal precio) {
        return precio != null && precio.signum() > 0 ? precio : null;
    }

    private String textoSeguro(String texto) {
        return texto == null ? "" : texto;
    }

    private String sha256(byte[] contenido) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(contenido));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }
}
