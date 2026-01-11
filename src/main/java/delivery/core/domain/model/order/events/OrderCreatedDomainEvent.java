package delivery.core.domain.model.order.events;

import delivery.core.domain.model.order.Order;
import libs.ddd.DomainEvent;
import lombok.Getter;
import java.util.UUID;

@Getter
public class OrderCreatedDomainEvent extends DomainEvent {
    private final UUID orderId;

    public OrderCreatedDomainEvent(Order order) {
        super(order);
        this.orderId = order.getId();
    }
}
