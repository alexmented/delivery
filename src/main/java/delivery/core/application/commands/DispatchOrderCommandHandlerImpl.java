package delivery.core.application.commands;

import delivery.core.domain.model.courier.Courier;
import delivery.core.domain.model.order.Order;
import delivery.core.domain.services.OrderDispatcher;
import delivery.core.ports.CourierRepository;
import delivery.core.ports.OrderRepository;
import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.Result;
import libs.errs.UnitResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DispatchOrderCommandHandlerImpl implements DispatchOrderCommandHandler {

    private final OrderRepository orderRepository;
    private final CourierRepository courierRepository;
    private final OrderDispatcher orderDispatcher;

    public DispatchOrderCommandHandlerImpl(OrderRepository orderRepository,
                                           CourierRepository courierRepository,
                                           OrderDispatcher orderDispatcher) {
        this.orderRepository = orderRepository;
        this.courierRepository = courierRepository;
        this.orderDispatcher = orderDispatcher;
    }

    @Override
    @Transactional
    public UnitResult<Error> handle(DispatchOrderCommand command) {
        var orderOpt = orderRepository.findById(command.orderId());
        if (orderOpt.isEmpty()) {
            return UnitResult.failure(GeneralErrors.notFound("order", command.orderId()));
        }
        Order order = orderOpt.get();

        List<Courier> freeCouriers = courierRepository.findAllFree();
        
        Result<Courier, Error> dispatchResult = orderDispatcher.dispatch(order, freeCouriers);
        if (dispatchResult.isFailure()) {
            return UnitResult.failure(dispatchResult.getError());
        }
        
        Courier assignedCourier = dispatchResult.getValue();

        orderRepository.save(order);
        courierRepository.save(assignedCourier);

        return UnitResult.success();
    }
}
