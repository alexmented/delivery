package delivery.core.application.commands.createorder;

import delivery.DomainEventPublisher;
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

import java.util.List;

@Service
public final class CreateOrderCommandHandlerImpl implements CreateOrderCommandHandler {
    private final OrderRepository orderRepository;
    private final UnitOfWork unitOfWork;
    private final GeoClient geoClient;
    private final DomainEventPublisher domainEventPublisher;

    public CreateOrderCommandHandlerImpl(OrderRepository orderRepository, 
                                         UnitOfWork unitOfWork,
                                         GeoClient geoClient,
                                         DomainEventPublisher domainEventPublisher) {
        this.orderRepository = orderRepository;
        this.unitOfWork = unitOfWork;
        this.geoClient = geoClient;
        this.domainEventPublisher = domainEventPublisher;
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
        domainEventPublisher.publish(List.of(order));

        return UnitResult.success();
    }
}
