package delivery.adapters.out.kafka;

import com.google.protobuf.Timestamp;
import delivery.core.domain.model.order.events.OrderCompletedDomainEvent;
import delivery.core.domain.model.order.events.OrderCreatedDomainEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import queues.order.OrderEventsProto.*;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class OrderEventsProducer {
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    @Value("${app.kafka.orders-events-topic}")
    private String topic;

    @EventListener
    public void handle(OrderCreatedDomainEvent event) {
        var integrationEvent = mapToProto(event);
        kafkaTemplate.send(topic, event.getOrderId().toString(), integrationEvent.toByteArray());
    }

    @EventListener
    public void handle(OrderCompletedDomainEvent event) {
        var integrationEvent = mapToProto(event);
        kafkaTemplate.send(topic, event.getOrderId().toString(), integrationEvent.toByteArray());
    }

    private OrderCreatedIntegrationEvent mapToProto(OrderCreatedDomainEvent event) {
        return OrderCreatedIntegrationEvent.newBuilder()
                .setEventId(event.getEventId().toString())
                .setEventType(event.getClass().getSimpleName())
                .setOccurredAt(toTimestamp(event.getOccurredOnUtc()))
                .setOrderId(event.getOrderId().toString())
                .build();
    }

    private OrderCompletedIntegrationEvent mapToProto(OrderCompletedDomainEvent event) {
        return OrderCompletedIntegrationEvent.newBuilder()
                .setEventId(event.getEventId().toString())
                .setEventType(event.getClass().getSimpleName())
                .setOccurredAt(toTimestamp(event.getOccurredOnUtc()))
                .setOrderId(event.getOrderId().toString())
                .setCourierId(event.getCourierId().toString())
                .build();
    }

    private Timestamp toTimestamp(Instant instant) {
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }
}
