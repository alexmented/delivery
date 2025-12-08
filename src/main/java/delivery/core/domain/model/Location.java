package delivery.core.domain.model;

import jakarta.persistence.Embeddable;
import libs.ddd.ValueObject;
import libs.errs.*;
import libs.errs.Error;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@Embeddable
@Getter
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Location extends ValueObject<Location> {
    private static final int MIN_COORDINATE = 1;
    private static final int MAX_COORDINATE = 10;

    private final int x;
    private final int y;

    public static Result<Location, Error> create(int x, int y) {
        var validation = UnitResult.combine(
                Err.againstOutOfRange(x, MIN_COORDINATE, MAX_COORDINATE, "x"),
                Err.againstOutOfRange(y, MIN_COORDINATE, MAX_COORDINATE, "y")
        );

        if (validation.isFailure()) {
            return Result.failure(validation.getError());
        }

        return Result.success(new Location(x, y));
    }

    public static Location createRandom() {
        int randomX = (int) (Math.random() * (MAX_COORDINATE - MIN_COORDINATE + 1)) + MIN_COORDINATE;
        int randomY = (int) (Math.random() * (MAX_COORDINATE - MIN_COORDINATE + 1)) + MIN_COORDINATE;
        return Location.create(randomX, randomY).getValue();
    }

    public Result<Integer, Error> distanceTo(Location target) {
        Except.againstNull(target, "target");
        int distance = Math.abs(this.x - target.x) + Math.abs(this.y - target.y);
        return Result.success(distance);
    }

    protected Iterable<Object> equalityComponents() {
        return List.of(this.x, this.y);
    }
}
