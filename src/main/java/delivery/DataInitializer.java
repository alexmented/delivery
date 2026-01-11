package delivery;

import delivery.core.application.commands.assignorder.AssignOrderCommand;
import delivery.core.application.commands.assignorder.AssignOrderCommandHandler;
import delivery.core.domain.model.Location;
import delivery.core.domain.model.courier.Courier;
import delivery.core.domain.model.order.Order;
import delivery.core.ports.CourierRepository;
import delivery.core.ports.OrderRepository;
import libs.errs.UnitResult;
import libs.errs.Result;
import libs.errs.Error;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AssignOrderCommandHandler handler;
    private final OrderRepository orderRepository;
    private final CourierRepository courierRepository;

    public DataInitializer(AssignOrderCommandHandler handler,
                           OrderRepository orderRepository,
                           CourierRepository courierRepository) {
        this.handler = handler;
        this.orderRepository = orderRepository;
        this.courierRepository = courierRepository;
    }

    @Override
    public void run(String... args) throws Exception {
//        Location courierLoc = Location.create(1, 1).getValue();
//        Courier courier = Courier.create("John", 20, courierLoc).getValue();
//        courierRepository.save(courier);
//        Location orderLoc = Location.create(5, 5).getValue();
//        Order order = Order.create(UUID.randomUUID(), orderLoc, 5).getValue();
//        orderRepository.save(order);
//
//        var command = AssignOrderCommand.create().getValue();
//        UnitResult<Error> result = handler.handle(command);
//
//        if (result.isSuccess()) {
//            System.out.println("SUCCESS: Order dispatched successfully!");
//        } else {
//            System.out.println("FAILURE: " + result.getError().getMessage());
//        }
    }
}
