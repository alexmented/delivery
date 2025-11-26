package delivery.core.domain.model.courier;

import delivery.core.domain.model.Location;
import libs.ddd.Aggregate;
import libs.errs.Err;
import libs.errs.Error;
import libs.errs.Except;
import libs.errs.Result;
import libs.errs.UnitResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
public final class Courier extends Aggregate<UUID> {

    private String name;
    private int speed;
    private Location location;
    private List<StoragePlace> storagePlaces = new ArrayList<>();

    private Courier(UUID id, String name, int speed, Location location) {
        super(id);
        this.name = name;
        this.speed = speed;
        this.location = location;
    }

    public static Result<Courier, Error> create(String name, int speed, Location location) {
        Except.againstNull(location, "Location");

        var validation = UnitResult.combine(
                Err.againstNullOrEmpty(name, "Name"),
                Err.againstZeroOrNegative(speed, "Speed")
        );

        if (validation.isFailure()) {
            return Result.failure(validation.getError());
        }

        var courier = new Courier(UUID.randomUUID(), name, speed, location);
        
        var bagResult = StoragePlace.create("Сумка", 10);
        if (bagResult.isFailure()) {
             return Result.failure(bagResult.getError());
        }
        courier.storagePlaces.add(bagResult.getValue());

        return Result.success(courier);
    }

    public UnitResult<Error> addStoragePlace(String name, int volume) {
        var result = StoragePlace.create(name, volume);
        if (result.isFailure()) {
            return UnitResult.failure(result.getError());
        }
        this.storagePlaces.add(result.getValue());
        return UnitResult.success();
    }

    public boolean isTakingOrderAvailable(int orderVolume) {
        return this.storagePlaces.stream().anyMatch(sp -> sp.isNewOrderAvailable(orderVolume));
    }

    public UnitResult<Error> takeOrder(UUID orderId, int orderVolume) {
        if (!isTakingOrderAvailable(orderVolume)) {
            return UnitResult.failure(Errors.courierCantTakeOrder());
        }

        var storagePlace = this.storagePlaces.stream()
                .filter(sp -> sp.isNewOrderAvailable(orderVolume))
                .findFirst()
                .orElseThrow();

        return storagePlace.putOrder(orderId, orderVolume);
    }

    public void completeOrder() {
        for (StoragePlace place : this.storagePlaces) {
            place.cleanOrder();
        }
    }

    public Result<Integer, Error> distanceToLocation(Location target) {
        var distanceResult = this.location.distanceTo(target);
        if (distanceResult.isFailure()) {
            return Result.failure(distanceResult.getError());
        }

        int distance = distanceResult.getValue();
        int steps = (int) Math.ceil((double) distance / this.speed);
        return Result.success(steps);
    }

    public UnitResult<Error> move(Location target) {
        if (target == null) {
            return UnitResult.failure(libs.errs.GeneralErrors.valueIsRequired("target"));
        }

        int difX = target.getX() - location.getX();
        int difY = target.getY() - location.getY();
        int cruisingRange = speed;

        int moveX = Math.max(-cruisingRange, Math.min(difX, cruisingRange));
        cruisingRange -= Math.abs(moveX);

        int moveY = Math.max(-cruisingRange, Math.min(difY, cruisingRange));

        Result<Location, Error> locationCreateResult = Location.create(
                location.getX() + moveX,
                location.getY() + moveY
        );

        if (locationCreateResult.isFailure()) {
            return UnitResult.failure(locationCreateResult.getError());
        }

        this.location = locationCreateResult.getValue();
        return UnitResult.success();
    }

    public static final class Errors {
        public static Error courierCantTakeOrder() {
            return Error.of(Courier.class.getSimpleName().toLowerCase(), "Нет места в хранилище");
        }
    }
}
