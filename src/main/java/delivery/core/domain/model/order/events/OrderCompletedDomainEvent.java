package delivery.core.domain.model.order.events;

import delivery.core.domain.model.order.Order;
import libs.ddd.DomainEvent;
import lombok.Getter;
import java.util.UUID;

@Getter
public class OrderCompletedDomainEvent extends DomainEvent {
    private final UUID orderId;
    private final UUID courierId;

    public OrderCompletedDomainEvent(Order order) {
        super(order);
        this.orderId = order.getId();
        this.courierId = order.getCourierId();
    }
}
