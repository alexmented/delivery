package delivery.core.domain.model.order;

import delivery.core.domain.model.Location;
import libs.ddd.Aggregate;
import libs.errs.*;
import libs.errs.Error;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
public final class Order extends Aggregate<UUID> {

    private final Location location;
    private final int volume;
    private Status status;
    private UUID courierId;

    private Order(UUID id, Location location, int volume) {
        super(id);
        this.location = location;
        this.volume = volume;
        this.status = Status.CREATED;
        this.courierId = null;
    }

    public static Result<Order, Error> create(UUID id, Location location, int volume) {
        Except.againstNull(location, "location");
        var validation = UnitResult.combine(
                Err.againstNullOrEmpty(id, "Id"),
                Err.againstZeroOrNegative(volume, "Volume")
        );

        if (validation.isFailure()) {
            return Result.failure(validation.getError());
        }

        return Result.success(new Order(id, location, volume));
    }

    public UnitResult<Error> assign(UUID courierId) {
        Except.againstNull(courierId, "CourierId");

        this.status = Status.ASSIGNED;
        this.courierId = courierId;
        return UnitResult.success();
    }

    public UnitResult<Error> complete() {
        if (this.status != Status.ASSIGNED) {
            return UnitResult.failure(Errors.orderMustBeAssigned());
        }

        this.status = Status.COMPLETED;
        return UnitResult.success();
    }

    public static final class Errors {
        public static Error orderMustBeAssigned() {
            return Error.of(Order.class.getSimpleName().toLowerCase(), "Необходимо назначить заказ");
        }
    }
}
