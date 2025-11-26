package delivery.core.domain.model.courier;

import delivery.core.domain.model.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CourierTest {

    private Courier courier;


    private Courier createCourier() {
        return Courier.create("John", 2, Location.create(1, 1).getValue()).getValue();
    }

    @BeforeEach
    void setUp() {
        courier = createCourier();
    }

    @Test
    void shouldBeCorrectWhenParamsAreCorrectOnCreated() {
        String name = "John Doe";
        int speed = 5;
        Location location = Location.create(1, 1).getValue();

        var result = Courier.create(name, speed, location);

        assertThat(result.isSuccess()).isTrue();
        var createdCourier = result.getValue();
        assertThat(createdCourier.getId()).isNotNull();
        assertThat(createdCourier.getName()).isEqualTo(name);
        assertThat(createdCourier.getSpeed()).isEqualTo(speed);
        assertThat(createdCourier.getLocation()).isEqualTo(location);
        assertThat(createdCourier.getStoragePlaces()).hasSize(1);
        assertThat(createdCourier.getStoragePlaces().getFirst().getName()).isEqualTo("Сумка");
        assertThat(createdCourier.getStoragePlaces().getFirst().getTotalVolume()).isEqualTo(10);
    }

    @Test
    void checkAddingStoragePlace() {
        var result = courier.addStoragePlace("Box", 20);

        assertThat(result.isSuccess()).isTrue();
        assertThat(courier.getStoragePlaces()).hasSize(2);
        assertThat(courier.getStoragePlaces().get(1).getName()).isEqualTo("Box");
        assertThat(courier.getStoragePlaces().get(1).getTotalVolume()).isEqualTo(20);
    }

    @Test
    void checkTakingOrderWithoutSpace() {
        var result = courier.takeOrder(UUID.randomUUID(), 5);

        assertThat(result.isSuccess()).isTrue();
        assertThat(courier.isTakingOrderAvailable(5)).isFalse();
    }

    @Test
    void checkCompleteOrder() {
        courier.takeOrder(UUID.randomUUID(), 5);

        courier.completeOrder();

        assertThat(courier.isTakingOrderAvailable(5)).isTrue();
    }

    @Test
    void checkDistanceToTarget() {
        var target = Location.create(5, 5).getValue();

        var result = courier.distanceToLocation(target);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue()).isEqualTo(4);
    }

    @Test
    void checkMove() {
        var target = Location.create(5, 5).getValue();

        var result = courier.move(target);

        assertThat(result.isSuccess()).isTrue();
        assertThat(courier.getLocation().getX()).isEqualTo(3);
        assertThat(courier.getLocation().getY()).isEqualTo(1);
    }

}
