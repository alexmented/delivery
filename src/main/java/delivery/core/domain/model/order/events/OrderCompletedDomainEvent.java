package delivery.core.domain.model.order.events;

import delivery.core.domain.model.order.Order;
import libs.ddd.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class OrderCompletedDomainEvent extends DomainEvent {
    private UUID orderId;
    private UUID courierId;

    public OrderCompletedDomainEvent(Order order) {
        super(order);
        this.orderId = order.getId();
        this.courierId = order.getCourierId();
    }
}
