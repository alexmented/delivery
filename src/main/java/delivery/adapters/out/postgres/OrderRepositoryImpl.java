package delivery.adapters.out.postgres;

import delivery.core.domain.model.order.Order;
import delivery.core.domain.model.order.Status;
import delivery.core.ports.OrderRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository jpa;

    public OrderRepositoryImpl(OrderJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(Order order) {
        jpa.save(order);
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return jpa.findById(id);
    }

    @Override
    public Optional<Order> findOneByStatus(Status status) {
        return jpa.findFirstByStatus(status);
    }

    @Override
    public List<Order> findAllByStatus(Status status) {
        return jpa.findAllByStatus(status);
    }
}
