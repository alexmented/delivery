package delivery.core.domain.services;

import delivery.core.domain.model.Location;
import delivery.core.domain.model.courier.Courier;
import delivery.core.domain.model.order.Order;
import libs.errs.Result;
import libs.errs.Error;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrderDispatcherImplTest {

    private OrderDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        dispatcher = new OrderDispatcherImpl();
    }

    @Test
    void checkClosestCourier() {
        Location orderLocation = Location.create(10, 10).getValue();
        Order order = Order.create(UUID.randomUUID(), orderLocation, 5).getValue();

        Location loc1 = Location.create(1, 1).getValue();
        Courier courier1 = Courier.create("John", 1, loc1).getValue();
        Location loc2 = Location.create(9, 9).getValue();
        Courier courier2 = Courier.create("Snow", 1, loc2).getValue();

        List<Courier> couriers = List.of(courier1, courier2);
        Result<Courier, Error> result = dispatcher.dispatch(order, couriers);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue()).isEqualTo(courier2);
    }

    @Test
    void checkOrderCreated() {
        Location orderLocation = Location.create(10, 10).getValue();
        Order order = Order.create(UUID.randomUUID(), orderLocation, 5).getValue();

        Location locationCourier = Location.create(1, 1).getValue();
        Courier courier1 = Courier.create("John", 1, locationCourier).getValue();

        List<Courier> couriers = List.of(courier1);

        Result<Courier, Error> result = dispatcher.dispatch(order, couriers);

        assertThat(result.isSuccess()).isTrue();
    }
}
