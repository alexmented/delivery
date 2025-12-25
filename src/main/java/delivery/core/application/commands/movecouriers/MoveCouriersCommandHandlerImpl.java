package delivery.core.application.commands.movecouriers;

import delivery.core.domain.model.courier.Courier;
import delivery.core.domain.model.order.Order;
import delivery.core.domain.model.order.Status;
import delivery.core.ports.CourierRepository;
import delivery.core.ports.OrderRepository;
import delivery.core.ports.UnitOfWork;
import libs.errs.Error;
import libs.errs.UnitResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public final class MoveCouriersCommandHandlerImpl implements MoveCouriersCommandHandler {
    private final OrderRepository orderRepository;
    private final CourierRepository courierRepository;
    private final UnitOfWork unitOfWork;

    public MoveCouriersCommandHandlerImpl(OrderRepository orderRepository, 
                                          CourierRepository courierRepository, 
                                          UnitOfWork unitOfWork) {
        this.orderRepository = orderRepository;
        this.courierRepository = courierRepository;
        this.unitOfWork = unitOfWork;
    }

    @Override
    @Transactional
    public UnitResult<Error> handle(MoveCouriersCommand command) {
        List<Order> assigned = orderRepository.findAllByStatus(Status.ASSIGNED);

        for (Order order : assigned) {
            if (order.getCourierId() == null) {
                continue;
            }

            Optional<Courier> courierOpt = courierRepository.findById(order.getCourierId());
            if (courierOpt.isEmpty()) {
                continue;
            }

            Courier courier = courierOpt.get();
            
            courier.move(order.getLocation());

            if (courier.getLocation().equals(order.getLocation())) {
                courier.completeOrder();
                order.complete();
            }

            courierRepository.save(courier);
            orderRepository.save(order);
        }

        unitOfWork.commit();
        return UnitResult.success();
    }
}
