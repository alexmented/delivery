package delivery.core.application.commands.createorder;

import delivery.core.domain.model.Location;
import delivery.core.domain.model.order.Order;
import delivery.core.ports.GeoClient;
import delivery.core.ports.OrderRepository;
import delivery.core.ports.UnitOfWork;
import libs.errs.Error;
import libs.errs.Result;
import libs.errs.UnitResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public final class CreateOrderCommandHandlerImpl implements CreateOrderCommandHandler {
    private final OrderRepository orderRepository;
    private final UnitOfWork unitOfWork;
    private final GeoClient geoClient;

    public CreateOrderCommandHandlerImpl(OrderRepository orderRepository, 
                                         UnitOfWork unitOfWork,
                                         GeoClient geoClient) {
        this.orderRepository = orderRepository;
        this.unitOfWork = unitOfWork;
        this.geoClient = geoClient;
    }

    @Override
    @Transactional
    public UnitResult<Error> handle(CreateOrderCommand command) {
        Result<Location, Error> locationResult = geoClient.getGeolocation(command.getStreet());
        
        Location location;
        if (locationResult.isFailure()) {
            location = Location.createRandom();
        } else {
            location = locationResult.getValue();
        }

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
