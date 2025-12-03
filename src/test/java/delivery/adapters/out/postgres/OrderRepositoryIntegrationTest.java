package delivery.adapters.out.postgres;

import delivery.core.domain.model.Location;
import delivery.core.domain.model.order.Order;
import delivery.core.domain.model.order.Status;
import delivery.core.ports.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OrderRepositoryIntegrationTest extends BasePostgresContainerTest {

    @Autowired
    OrderRepository repository;

    @Test
    void checkSavingOrder() {
        var orderId = UUID.randomUUID();
        var location = Location.create(5, 5).getValue();
        var order = Order.create(orderId, location, 10).getValue();

        repository.save(order);
        var loaded = repository.findById(order.getId());

        assertThat(loaded).isPresent();
        assertThat(loaded.get().getStatus()).isEqualTo(order.getStatus());
        assertThat(loaded.get().getVolume()).isEqualTo(order.getVolume());
        assertThat(loaded.get().getId()).isEqualTo(order.getId());
    }

    @Test
    void checkFindByStatus() {
        var orderId = UUID.randomUUID();
        var location = Location.create(5, 5).getValue();
        var order = Order.create(orderId, location, 10).getValue();
        repository.save(order);

        var loaded = repository.findAllByStatus(Status.CREATED);

        assertThat(loaded).extracting(Order::getId).contains(order.getId());
    }
}
