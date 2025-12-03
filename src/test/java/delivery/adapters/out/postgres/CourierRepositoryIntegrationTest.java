package delivery.adapters.out.postgres;

import delivery.core.domain.model.Location;
import delivery.core.domain.model.courier.Courier;
import delivery.core.ports.CourierRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CourierRepositoryIntegrationTest extends BasePostgresContainerTest {

    @Autowired
    CourierRepository repository;

    @Test
    void checkSavingCouriers() {
        var location = Location.create(1, 1).getValue();
        var courier = Courier.create("John", 10, location).getValue();

        repository.save(courier);
        var loaded = repository.findById(courier.getId());

        assertThat(loaded).isPresent();
    }

    @Test
    void checkFreeCouriers() {
        var location1 = Location.create(1, 1).getValue();
        var courier1 = Courier.create("John", 10, location1).getValue();
        repository.save(courier1);

        var location2 = Location.create(2, 2).getValue();
        var courier2 = Courier.create("Snow", 10, location2).getValue();
        courier2.takeOrder(UUID.randomUUID(), 5); // Assign order to make it busy
        repository.save(courier2);

        var freeCouriers = repository.findAllFree();

        assertThat(freeCouriers).extracting(Courier::getId)
                .contains(courier1.getId()).doesNotContain(courier2.getId());
    }
}
