package delivery.core.application.commands.assignorder;

import delivery.core.domain.model.courier.Courier;
import delivery.core.domain.model.order.Order;
import delivery.core.domain.model.order.Status;
import delivery.core.domain.services.OrderDispatcher;
import delivery.core.ports.CourierRepository;
import delivery.core.ports.OrderRepository;
import delivery.core.ports.UnitOfWork;
import libs.errs.Error;
import libs.errs.Result;
import libs.errs.UnitResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public final class AssignOrderCommandHandlerImpl implements AssignOrderCommandHandler {
    private final OrderRepository orderRepository;
    private final CourierRepository courierRepository;
    private final UnitOfWork unitOfWork;
    private final OrderDispatcher orderDispatcher;

    public AssignOrderCommandHandlerImpl(OrderRepository orderRepository,
                                         CourierRepository courierRepository,
                                         UnitOfWork unitOfWork,
                                         OrderDispatcher orderDispatcher) {
        this.orderRepository = orderRepository;
        this.courierRepository = courierRepository;
        this.unitOfWork = unitOfWork;
        this.orderDispatcher = orderDispatcher;
    }

    @Override
    @Transactional
    public UnitResult<Error> handle(AssignOrderCommand command) {
        List<Courier> freeCouriers = courierRepository.findAllFree();
        if (freeCouriers.isEmpty()) {
            return UnitResult.success(); 
        }

        List<Order> unassignedOrders = orderRepository.findAllByStatus(Status.CREATED);
        if (unassignedOrders.isEmpty()) {
            return UnitResult.success(); 
        }
        Order order = unassignedOrders.getFirst();

        Result<Courier, Error> dispatchResult = orderDispatcher.dispatch(order, freeCouriers);
        
        if (dispatchResult.isFailure()) {
            return UnitResult.success();
        }

        Courier bestCourier = dispatchResult.getValue();

        orderRepository.save(order);
        courierRepository.save(bestCourier);
        unitOfWork.commit();

        return UnitResult.success();
    }
}
