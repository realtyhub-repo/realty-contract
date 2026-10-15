package service.contratos.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import service.contratos.config.RabbitConfig;
import service.contratos.dto.event.ContractCancelledEvent;
import service.contratos.dto.event.ContractSignedEvent;
import service.contratos.entity.Contrato;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContratoEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publicarFirmado(Contrato contrato) {
        ContractSignedEvent evento = new ContractSignedEvent(
                contrato.getId(),
                contrato.getLeadId(),
                contrato.getPropiedadId(),
                contrato.getClienteId(),
                contrato.getAgenteId(),
                contrato.getTipo(),
                contrato.getPrecioAcordado(),
                contrato.getFirmadoEn());
        enviarTrasCommit(RabbitConfig.RK_CONTRACT_SIGNED, evento);
    }

    public void publicarCancelado(Contrato contrato) {
        ContractCancelledEvent evento = new ContractCancelledEvent(
                contrato.getId(),
                contrato.getLeadId(),
                contrato.getPropiedadId(),
                contrato.getClienteId(),
                contrato.getAgenteId());
        enviarTrasCommit(RabbitConfig.RK_CONTRACT_CANCELLED, evento);
    }

    private void enviarTrasCommit(String routingKey, Object evento) {
        Runnable envio = () -> {
            try {
                rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, routingKey, evento);
            } catch (AmqpException e) {
                log.error("No se pudo publicar {}: {}", routingKey, e.getMessage());
            }
        };

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    envio.run();
                }
            });
        } else {
            envio.run();
        }
    }
}
