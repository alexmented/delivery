package delivery.core.application.commands.createorder;

import delivery.core.domain.model.Location;
import delivery.core.domain.model.order.Order;
import delivery.core.ports.OrderRepository;
import delivery.core.ports.UnitOfWork;
import libs.errs.Error;
import libs.errs.UnitResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public final class CreateOrderCommandHandlerImpl implements CreateOrderCommandHandler {
    private final OrderRepository orderRepository;
    private final UnitOfWork unitOfWork;

    public CreateOrderCommandHandlerImpl(OrderRepository orderRepository, UnitOfWork unitOfWork) {
        this.orderRepository = orderRepository;
        this.unitOfWork = unitOfWork;
    }

    @Override
    @Transactional
    public UnitResult<Error> handle(CreateOrderCommand command) {
        Location location = Location.createRandom();

        var orderResult = Order.create(command.getOrderId(), location, command.getVolume());
        if (orderResult.isFailure()) {
            return UnitResult.failure(orderResult.getError());
        }

        Order order = orderResult.getValue();
        orderRepository.save(order);
        unitOfWork.commit();

        return UnitResult.success();
    }
}
