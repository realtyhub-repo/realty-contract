package service.contratos.service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;
import service.contratos.entity.Contrato;
import service.contratos.enums.EstadoContrato;
import service.contratos.exceptions.PdfGeneracionException;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Component
public class ContratoPdfGenerator {

    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] generar(Contrato contrato) {
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        Document documento = new Document(PageSize.A4, 56, 56, 64, 64);

        try {
            PdfWriter.getInstance(documento, salida);
            documento.open();

            if (contrato.getEstado() == EstadoContrato.BORRADOR) {
                documento.add(marcaBorrador());
            }

            agregarContenido(documento, contrato.getContenido());

            if (contrato.getEstado() == EstadoContrato.FIRMADO) {
                agregarFirmas(documento, contrato);
            }

            documento.close();
            return salida.toByteArray();

        } catch (DocumentException e) {
            throw new PdfGeneracionException("No se pudo generar el PDF del contrato", e);
        }
    }

    private Paragraph marcaBorrador() {
        Font fuente = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Font.NORMAL, Color.GRAY);
        Paragraph marca = new Paragraph("BORRADOR - SIN VALOR LEGAL", fuente);
        marca.setAlignment(Element.ALIGN_RIGHT);
        marca.setSpacingAfter(16);
        return marca;
    }

    private void agregarContenido(Document documento, String contenido) throws DocumentException {
        Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 15, Font.NORMAL, Color.BLACK);
        Font fuenteCuerpo = FontFactory.getFont(FontFactory.HELVETICA, 11, Font.NORMAL, Color.BLACK);

        boolean esTitulo = true;
        for (String linea : contenido.split("\n")) {
            if (esTitulo) {
                Paragraph titulo = new Paragraph(linea, fuenteTitulo);
                titulo.setAlignment(Element.ALIGN_CENTER);
                titulo.setSpacingAfter(22);
                documento.add(titulo);
                esTitulo = false;
                continue;
            }

            Paragraph parrafo = new Paragraph(linea.isBlank() ? " " : linea, fuenteCuerpo);
            parrafo.setLeading(17);
            parrafo.setSpacingAfter(4);
            parrafo.setAlignment(Element.ALIGN_JUSTIFIED);
            documento.add(parrafo);
        }
    }

    private void agregarFirmas(Document documento, Contrato contrato) throws DocumentException {
        Font fuente = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL, Color.BLACK);
        Font fuenteNota = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, Color.GRAY);

        Paragraph espacio = new Paragraph(" ");
        espacio.setSpacingBefore(36);
        documento.add(espacio);

        PdfPTable tabla = new PdfPTable(2);
        tabla.setWidthPercentage(100);
        tabla.addCell(celdaFirma("Representante", contrato.getNombreAgente(), fuente));
        tabla.addCell(celdaFirma("Cliente", contrato.getNombreCliente(), fuente));
        documento.add(tabla);

        Paragraph nota = new Paragraph(
                "Firmado electrónicamente el " + contrato.getFirmadoEn().format(FECHA_HORA)
                        + " (hora de Colombia). Identificador del contrato: " + contrato.getId(),
                fuenteNota);
        nota.setSpacingBefore(28);
        documento.add(nota);
    }

    private PdfPCell celdaFirma(String rol, String nombre, Font fuente) {
        PdfPCell celda = new PdfPCell();
        celda.setBorder(Rectangle.NO_BORDER);
        celda.setPaddingRight(20);
        celda.addElement(new Phrase("______________________________", fuente));
        celda.addElement(new Phrase(nombre, fuente));
        celda.addElement(new Phrase(rol, fuente));
        return celda;
    }
}
