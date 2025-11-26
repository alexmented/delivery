package delivery.core.domain.model.courier;

import libs.ddd.BaseEntity;
import libs.errs.Err;
import libs.errs.Error;
import libs.errs.Result;
import libs.errs.UnitResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
public final class StoragePlace extends BaseEntity<UUID> {

    @Getter
    private String name;

    @Getter
    private int totalVolume;

    @Getter
    private UUID orderId;

    private StoragePlace(UUID id, String name, int totalVolume, UUID orderId) {
        super(id);
        this.name = name;
        this.totalVolume = totalVolume;
        this.orderId = orderId;
    }

    public static Result<StoragePlace, Error> create(String name, int totalVolume) {
        return create(name, totalVolume, null);
    }

    public static Result<StoragePlace, Error> create(String name, int totalVolume, UUID orderId) {
        var validation = UnitResult.combine(
                Err.againstNullOrEmpty(name, "Name"),
                Err.againstZeroOrNegative(totalVolume, "TotalVolume")
        );

        if (validation.isFailure()) {
            return Result.failure(validation.getError());
        }

        return Result.success(new StoragePlace(UUID.randomUUID(), name, totalVolume, orderId));
    }

    public boolean isNewOrderAvailable(int orderVolume) {
        return this.orderId == null && orderVolume <= this.totalVolume;
    }

    public UnitResult<Error> putOrder(UUID orderId, int orderVolume) {
        var validation = Err.againstNullOrEmpty(orderId, "OrderId");
        if (validation.isFailure()) {
            return UnitResult.failure(validation.getError());
        }

        if (!isNewOrderAvailable(orderVolume)) {
            return UnitResult.failure(Errors.unableToPutOrder());
        }

        this.orderId = orderId;
        return UnitResult.success();
    }

    public void cleanOrder() {
        this.orderId = null;
    }

    public boolean isEmpty() {
        return this.orderId == null;
    }

    public static final class Errors {
        public static Error unableToPutOrder() {
            return Error.of(StoragePlace.class.getSimpleName().toLowerCase(), "Хранилище занято");
        }
    }
}
