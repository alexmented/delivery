package delivery.core.domain.model.order;

import delivery.core.domain.model.Location;
import libs.errs.GeneralErrors;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {
    private Order createOrder() {
        return Order.create(UUID.randomUUID(), Location.create(1, 1).getValue(), 10).getValue();
    }

    @Test
    void checkCorrectCreation() {
        UUID id = UUID.randomUUID();
        Location location = Location.create(1, 1).getValue();
        int volume = 10;

        var result = Order.create(id, location, volume);

        assertThat(result.isSuccess()).isTrue();
        var order = result.getValue();
        assertThat(order.getId()).isEqualTo(id);
        assertThat(order.getLocation()).isEqualTo(location);
        assertThat(order.getVolume()).isEqualTo(volume);
        assertThat(order.getStatus()).isEqualTo(Status.CREATED);
        assertThat(order.getCourierId()).isNull();
    }

    @Test
    void checkCreationErrorIdNull() {
        Location location = Location.create(1, 1).getValue();

        var result = Order.create(null, location, 10);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isEqualTo(GeneralErrors.valueIsRequired("Id"));
    }

    @Test
    void checkAssign() {
        var order = createOrder();
        UUID courierId = UUID.randomUUID();

        var result = order.assign(courierId);

        assertThat(result.isSuccess()).isTrue();
        assertThat(order.getStatus()).isEqualTo(Status.ASSIGNED);
        assertThat(order.getCourierId()).isEqualTo(courierId);
    }

    @Test
    void checkComplete() {
        var order = createOrder();
        order.assign(UUID.randomUUID());

        var result = order.complete();

        assertThat(result.isSuccess()).isTrue();
        assertThat(order.getStatus()).isEqualTo(Status.COMPLETED);
    }
}
