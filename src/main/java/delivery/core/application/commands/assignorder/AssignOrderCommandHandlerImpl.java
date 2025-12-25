package delivery.core.application.commands.assignorder;

import delivery.core.domain.model.courier.Courier;
import delivery.core.domain.model.order.Order;
import delivery.core.domain.model.order.Status;
import delivery.core.ports.CourierRepository;
import delivery.core.ports.OrderRepository;
import delivery.core.ports.UnitOfWork;
import libs.errs.Error;
import libs.errs.UnitResult;
import libs.errs.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public final class AssignOrderCommandHandlerImpl implements AssignOrderCommandHandler {
    private final OrderRepository orderRepository;
    private final CourierRepository courierRepository;
    private final UnitOfWork unitOfWork;

    public AssignOrderCommandHandlerImpl(OrderRepository orderRepository,
                                         CourierRepository courierRepository,
                                         UnitOfWork unitOfWork) {
        this.orderRepository = orderRepository;
        this.courierRepository = courierRepository;
        this.unitOfWork = unitOfWork;
    }

    @Override
    @Transactional
    public UnitResult<Error> handle(AssignOrderCommand command) {
        List<Courier> freeCouriers = courierRepository.findAllFree();
        if (freeCouriers.isEmpty()) {
            return UnitResult.success(); 
        }

        List<Order> unassignedOrders = orderRepository.findAllByStatus(Status.CREATED);;
        if (unassignedOrders.isEmpty()) {
            return UnitResult.success(); 
        }
        Order order = unassignedOrders.getFirst();

        Courier bestCourier = null;
        int minSteps = Integer.MAX_VALUE;

        for (Courier courier : freeCouriers) {
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
                bestCourier = courier;
            }
        }

        if (bestCourier == null) {
            return UnitResult.success();
        }

        UnitResult<Error> assignResult = order.assign(bestCourier.getId());
        if (assignResult.isFailure()) return assignResult;

        UnitResult<Error> takeResult = bestCourier.takeOrder(order.getId(), order.getVolume());
        if (takeResult.isFailure()) return takeResult;

        orderRepository.save(order);
        courierRepository.save(bestCourier);
        unitOfWork.commit();

        return UnitResult.success();
    }
}
