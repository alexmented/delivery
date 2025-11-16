package delivery.core.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LocationTest {

    @Test
    void testCreateWithValidCoordinates() {
        var result = Location.create(5, 7);
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue().getX()).isEqualTo(5);
        assertThat(result.getValue().getY()).isEqualTo(7);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -10, 11, 15, 100})
    void testCreateWithInvalidX(int invalidX) {
        var result = Location.create(invalidX, 5);
        assertThat(result.isFailure()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -10, 11, 15, 100})
    void testCreateWithInvalidY(int invalidY) {
        var result = Location.create(5, invalidY);
        assertThat(result.isFailure()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 5, 8, 10})
    void testCreateWithValidCoordinates(int coordinate) {
        var result = Location.create(coordinate, coordinate);
        assertThat(result.isSuccess()).isTrue();
    }

    @Test
    void testCreateRandom() {
        var location = Location.createRandom();
        assertThat(location).isNotNull();
        assertThat(location.getX()).isBetween(1, 10);
        assertThat(location.getY()).isBetween(1, 10);
    }

    @Test
    void testDistanceTo() {
        var location1 = Location.create(2, 3).getValue();
        var location2 = Location.create(5, 7).getValue();
        var result = location1.distanceTo(location2);
        assertThat(result.getValue()).isEqualTo(7);
    }

    @Test
    void testDistanceToNull() {
        var location = Location.create(5, 5).getValue();

        org.assertj.core.api.Assertions.assertThatThrownBy(
                        () -> location.distanceTo(null)
                ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testDistanceToSameLocation() {
        var location = Location.create(5, 5).getValue();
        var result = location.distanceTo(location);
        assertThat(result.getValue()).isEqualTo(0);
    }

    @Test
    void testEquality() {
        var location1 = Location.create(5, 7).getValue();
        var location2 = Location.create(5, 7).getValue();
        assertThat(location1).isEqualTo(location2);
    }
}