package delivery.adapters.in.kafka;

import delivery.core.application.commands.createorder.CreateOrderCommand;
import delivery.core.application.commands.createorder.CreateOrderCommandHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import queues.basket.BasketEventsProto;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BasketEventsConsumer {

    private final CreateOrderCommandHandler createOrderCommandHandler;

    @KafkaListener(topics = "baskets.events", groupId = "delivery-service")
    public void listen(byte[] message) {
        try {
            var event = BasketEventsProto.BasketConfirmedIntegrationEvent.parseFrom(message);

            String street = "Unknown Street";
            if (event.hasAddress()) {
                street = event.getAddress().getStreet();
            }

            int volume = event.getVolume();

            var commandResult = CreateOrderCommand.create(UUID.fromString(event.getBasketId()), street, volume);
            if (commandResult.isFailure()) {
                throw new RuntimeException("Invalid command: " + commandResult.getError());
            }

            var command = commandResult.getValue();
            var handleResult = createOrderCommandHandler.handle(command);

            if (handleResult.isFailure()) {
                throw new RuntimeException("Failed to handle command: " + handleResult.getError());
            }
        } catch (com.google.protobuf.InvalidProtocolBufferException ex) {
            throw new RuntimeException("Failed to parse protobuf message", ex);
        }
    }
}
