package service.contratos.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import service.contratos.config.RabbitConfig;
import service.contratos.dto.event.LeadOfertaAceptadaEvent;
import service.contratos.exceptions.NoReintentableException;
import service.contratos.service.ContratoService;

@Slf4j
@Component
@RequiredArgsConstructor
public class LeadOfertaAceptadaListener {

    private final ContratoService contratoService;

    @RabbitListener(queues = RabbitConfig.QUEUE_LEAD_OFERTA_ACEPTADA)
    public void alAceptarseOferta(LeadOfertaAceptadaEvent evento) {
        try {
            contratoService.crearBorradorDesdeLead(evento);
        } catch (NoReintentableException e) {
            log.error("Evento lead.oferta_aceptada descartado (lead {}): {}", evento.leadId(), e.getMessage());
        }
    }
}
