package delivery.core.domain.services;

import delivery.core.domain.model.courier.Courier;
import delivery.core.domain.model.order.Order;
import delivery.core.domain.model.order.Status;
import libs.errs.Error;
import libs.errs.Except;
import libs.errs.Result;
import libs.errs.UnitResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderDispatcherImpl implements OrderDispatcher {
    public Result<Courier, Error> dispatch(Order order, List<Courier> couriers) {
        Except.againstNull(order, "Order");
        Except.againstNull(couriers, "Couriers");
        
        if (order.getStatus() != Status.CREATED) {
            return Result.failure(Errors.orderNotCreated());
        }

        if (couriers.isEmpty()) {
            return Result.failure(Errors.noCouriersAvailable());
        }

        Courier resultCourier = null;
        int minSteps = Integer.MAX_VALUE;

        for (Courier courier : couriers) {
            if (!courier.isTakingOrderAvailable(order.getVolume())) {
                continue;
            }

            Result<Integer, Error> stepsResult = courier.distanceToLocation(order.getLocation());
            
            if (stepsResult.isFailure()) {
                continue;
            }

            int steps = stepsResult.getValue();

            if (steps < minSteps) {
                minSteps = steps;
                resultCourier = courier;
            }
        }

        if (resultCourier == null) {
            return Result.failure(Errors.noSuitableCourierFound());
        }

        UnitResult<Error> assignResult = order.assign(resultCourier.getId());
        if (assignResult.isFailure()) {
             return Result.failure(assignResult.getError());
        }

        UnitResult<Error> takeOrderResult = resultCourier.takeOrder(order.getId(), order.getVolume());
        if (takeOrderResult.isFailure()) {
            return Result.failure(takeOrderResult.getError());
        }

        return Result.success(resultCourier);
    }

    public static final class Errors {
        public static Error noSuitableCourierFound() {
            return Error.of("order_dispatcher", "No courier found");
        }
        public static Error noCouriersAvailable() {
            return Error.of("no_couriers", "No couriers available");
        }
        public static Error orderNotCreated() {
            return Error.of("order_dispatcher", "Order not Created");
        }
    }
}
