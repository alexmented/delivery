package delivery.core.ports;

import delivery.core.domain.model.order.Order;
import delivery.core.domain.model.order.Status;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    void save(Order order);
    Optional<Order> findById(UUID id);
    Optional<Order> findOneByStatus(Status status);
    List<Order> findAllByStatus(Status status);
}
