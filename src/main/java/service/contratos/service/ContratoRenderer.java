package service.contratos.service;

import org.springframework.stereotype.Component;
import service.contratos.entity.Contrato;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class ContratoRenderer {

    private static final Pattern MARCADOR = Pattern.compile("\\{\\{(\\w+)}}");
    private static final ZoneId BOGOTA = ZoneId.of("America/Bogota");
    private static final Locale ES_CO = Locale.of("es", "CO");
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", ES_CO);

    public String render(String cuerpo, Contrato contrato) {
        Map<String, String> valores = new HashMap<>();
        valores.put("fecha", fecha(contrato));
        valores.put("nombreAgente", contrato.getNombreAgente());
        valores.put("nombreCliente", contrato.getNombreCliente());
        valores.put("direccion", contrato.getDireccionPropiedad());
        valores.put("ciudad", contrato.getCiudadPropiedad());
        valores.put("precio", precio(contrato));

        String normalizado = cuerpo.lines()
                .map(String::strip)
                .collect(Collectors.joining("\n"));

        return MARCADOR.matcher(normalizado)
                .replaceAll(m -> Matcher.quoteReplacement(valores.getOrDefault(m.group(1), "")));
    }

    private String fecha(Contrato contrato) {
        LocalDate dia = contrato.getFirmadoEn() != null
                ? contrato.getFirmadoEn().toLocalDate()
                : LocalDate.now(BOGOTA);
        return dia.format(FECHA);
    }

    private String precio(Contrato contrato) {
        if (contrato.getPrecioAcordado() == null) {
            return "Por definir";
        }
        NumberFormat formato = NumberFormat.getCurrencyInstance(ES_CO);
        formato.setMinimumFractionDigits(0);
        formato.setMaximumFractionDigits(0);
        return formato.format(contrato.getPrecioAcordado());
    }
}
