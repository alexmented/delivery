package delivery.core.domain.model.courier;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

class StoragePlaceTest {

    @Test
    void checkCorrectCreation() {
        String name = "Backpack";
        int volume = 100;

        var result = StoragePlace.create(name, volume);

        assertThat(result.isSuccess()).isTrue();
        var storage = result.getValue();
        assertThat(storage.getId()).isNotNull();
        assertThat(storage.getName()).isEqualTo(name);
        assertThat(storage.getTotalVolume()).isEqualTo(volume);
        assertThat(storage.getOrderId()).isNull();
    }

    @Test
    void checkCorrectCreationWithOrderID() {
        String name = "Backpack";
        int volume = 500;
        UUID orderId = UUID.randomUUID();

        var result = StoragePlace.create(name, volume, orderId);

        assertThat(result.isSuccess()).isTrue();
        var storage = result.getValue();
        assertThat(storage.isEmpty()).isFalse();
    }

    @Test
    void checkOrderSuitsVolume() {
        var storage = StoragePlace.create("Backpack", 50).getValue();
        UUID orderId = UUID.randomUUID();

        var result = storage.putOrder(orderId, 50);

        assertThat(result.isSuccess()).isTrue();
        assertThat(storage.getOrderId()).isEqualTo(orderId);
        assertThat(storage.isEmpty()).isFalse();
    }

    @Test
    void checkVolumeIsLarge() {
        var storage = StoragePlace.create("Backpack", 50).getValue();
        UUID orderId = UUID.randomUUID();

        var result = storage.putOrder(orderId, 51);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isEqualTo(StoragePlace.Errors.unableToPutOrder());
    }

    @Test
    void checkStorageOccupied() {
        var storage = StoragePlace.create("Backpack", 50).getValue();
        storage.putOrder(UUID.randomUUID(), 10);

        var result = storage.putOrder(UUID.randomUUID(), 10);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isEqualTo(StoragePlace.Errors.unableToPutOrder());
    }

    @Test
    void canTakeOrder() {
        var storage = StoragePlace.create("Backpack", 50).getValue();
        storage.putOrder(UUID.randomUUID(), 10);

        storage.takeOrder();

        assertThat(storage.isEmpty()).isTrue();
        assertThat(storage.getOrderId()).isNull();
    }
}
