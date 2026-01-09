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
            // System.out.println("No free couriers found");
            return UnitResult.success(); 
        }

        List<Order> unassignedOrders = orderRepository.findAllByStatus(Status.CREATED);;
        if (unassignedOrders.isEmpty()) {
            // System.out.println("No unassigned orders found");
            return UnitResult.success(); 
        }
        Order order = unassignedOrders.getFirst();
        System.out.println("Assigning order " + order.getId() + " (vol: " + order.getVolume() + ")");

        Courier bestCourier = null;
        int minSteps = Integer.MAX_VALUE;

        for (Courier courier : freeCouriers) {
            if (!courier.isTakingOrderAvailable(order.getVolume())) {
                System.out.println("Courier " + courier.getName() + " skipped: not enough capacity");
                continue;
            }

            Result<Integer, Error> stepsResult = courier.distanceToLocation(order.getLocation());
            if (stepsResult.isFailure()) {
                System.out.println("Courier " + courier.getName() + " skipped: unreachable (" + stepsResult.getError() + ")");
                continue;
            }

            int steps = stepsResult.getValue();
            System.out.println("Courier " + courier.getName() + " steps: " + steps);
            if (steps < minSteps) {
                minSteps = steps;
                bestCourier = courier;
            }
        }

        if (bestCourier == null) {
            System.out.println("No suitable courier found for order " + order.getId());
            return UnitResult.success();
        }

        System.out.println("Selected courier: " + bestCourier.getName());

        UnitResult<Error> assignResult = order.assign(bestCourier.getId());
        if (assignResult.isFailure()) {
            System.err.println("Failed to assign order: " + assignResult.getError());
            return assignResult;
        }

        UnitResult<Error> takeResult = bestCourier.takeOrder(order.getId(), order.getVolume());
        if (takeResult.isFailure()) {
            System.err.println("Failed to take order: " + takeResult.getError());
            return takeResult;
        }

        orderRepository.save(order);
        courierRepository.save(bestCourier);
        unitOfWork.commit();
        
        System.out.println("Order " + order.getId() + " assigned to " + bestCourier.getName());

        return UnitResult.success();
    }
}
