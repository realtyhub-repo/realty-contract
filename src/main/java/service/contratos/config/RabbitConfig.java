package service.contratos.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "realtyhub.events";
    public static final String DEAD_LETTER_EXCHANGE = "realtyhub.events.dlx";

    public static final String RK_LEAD_OFERTA_ACEPTADA = "lead.oferta_aceptada";
    public static final String RK_CONTRACT_SIGNED = "contract.signed";
    public static final String RK_CONTRACT_CANCELLED = "contract.cancelled";

    public static final String QUEUE_LEAD_OFERTA_ACEPTADA = "contratos.lead-oferta-aceptada";
    public static final String QUEUE_LEAD_OFERTA_ACEPTADA_DLQ = "contratos.lead-oferta-aceptada.dlq";

    @Bean
    public JacksonJsonMessageConverter jacksonJsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public TopicExchange eventsExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE, true, false);
    }

    @Bean
    public Queue leadOfertaAceptadaQueue() {
        return QueueBuilder.durable(QUEUE_LEAD_OFERTA_ACEPTADA)
                .deadLetterExchange(DEAD_LETTER_EXCHANGE)
                .deadLetterRoutingKey(QUEUE_LEAD_OFERTA_ACEPTADA_DLQ)
                .build();
    }

    @Bean
    public Queue leadOfertaAceptadaDeadLetterQueue() {
        return QueueBuilder.durable(QUEUE_LEAD_OFERTA_ACEPTADA_DLQ).build();
    }

    @Bean
    public Binding leadOfertaAceptadaBinding(Queue leadOfertaAceptadaQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(leadOfertaAceptadaQueue).to(eventsExchange).with(RK_LEAD_OFERTA_ACEPTADA);
    }

    @Bean
    public Binding leadOfertaAceptadaDeadLetterBinding(Queue leadOfertaAceptadaDeadLetterQueue,
                                                       DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(leadOfertaAceptadaDeadLetterQueue)
                .to(deadLetterExchange)
                .with(QUEUE_LEAD_OFERTA_ACEPTADA_DLQ);
    }
}
