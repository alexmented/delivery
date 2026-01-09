package delivery.adapters.in.http;

import delivery.adapters.in.http.api.ApiApi;
import delivery.adapters.in.http.model.Courier;
import delivery.adapters.in.http.model.CreateCourierResponse;
import delivery.adapters.in.http.model.CreateOrderResponse;
import delivery.adapters.in.http.model.Location;
import delivery.adapters.in.http.model.NewCourier;
import delivery.adapters.in.http.model.Order;
import lombok.RequiredArgsConstructor;
import delivery.core.application.commands.createcourier.CreateCourierCommand;
import delivery.core.application.commands.createcourier.CreateCourierCommandHandler;
import delivery.core.application.commands.createorder.CreateOrderCommand;
import delivery.core.application.commands.createorder.CreateOrderCommandHandler;
import delivery.core.application.queries.getcouriers.GetCouriersQuery;
import delivery.core.application.queries.getcouriers.GetCouriersQueryHandler;
import delivery.core.application.queries.getcouriers.GetCouriersResponse;
import delivery.core.application.queries.getorders.GetOrdersQuery;
import delivery.core.application.queries.getorders.GetOrdersQueryHandler;
import delivery.core.application.queries.getorders.GetOrdersResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class DeliveryController implements ApiApi {
    private final CreateOrderCommandHandler createOrderCommandHandler;
    private final GetOrdersQueryHandler getOrdersQueryHandler;
    private final CreateCourierCommandHandler createCourierCommandHandler;
    private final GetCouriersQueryHandler getCouriersQueryHandler;

    @Override
    public ResponseEntity<CreateOrderResponse> createOrder() {
        UUID orderId = UUID.randomUUID();
        
        var createCommandResult = CreateOrderCommand.create(orderId, "Test Street", 10);
        if (createCommandResult.isFailure())
            return ResponseEntity.badRequest().build();
        var command = createCommandResult.getValue();

        var handleCommandResult = this.createOrderCommandHandler.handle(command);
        if (handleCommandResult.isFailure())
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        CreateOrderResponse response = new CreateOrderResponse();
        response.setOrderId(orderId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<List<Order>> getOrders() {
        var createQueryResult = GetOrdersQuery.create();
        if (createQueryResult.isFailure())
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        var query = createQueryResult.getValue();

        var handleQueryResult = this.getOrdersQueryHandler.handle(query);
        if (handleQueryResult.isFailure())
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        List<GetOrdersResponse> queryResponse = handleQueryResult.getValue();
        List<Order> orders = queryResponse.stream()
                .map(this::mapToOpenApiModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(orders);
    }

    @Override
    public ResponseEntity<CreateCourierResponse> createCourier(@Nullable NewCourier newCourier) {
        var createCommandResult = CreateCourierCommand.create(newCourier.getName(), newCourier.getSpeed());
        if (createCommandResult.isFailure())
            return ResponseEntity.badRequest().build();
        var command = createCommandResult.getValue();

        var handleCommandResult = this.createCourierCommandHandler.handle(command);
        if (handleCommandResult.isFailure())
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        CreateCourierResponse response = new CreateCourierResponse();
        response.setCourierId(UUID.randomUUID());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<List<Courier>> getCouriers() {
        var createQueryResult = GetCouriersQuery.create();
        if (createQueryResult.isFailure())
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        var query = createQueryResult.getValue();

        var handleQueryResult = this.getCouriersQueryHandler.handle(query);
        if (handleQueryResult.isFailure())
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        List<GetCouriersResponse> queryResponse = handleQueryResult.getValue();
        List<Courier> couriers = queryResponse.stream()
                .map(this::mapToOpenApiModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(couriers);
    }

    private Order mapToOpenApiModel(GetOrdersResponse response) {
        Order order = new Order();
        order.setId(response.getId());
        
        Location location = new Location();
        location.setX(response.getLocation().getX());
        location.setY(response.getLocation().getY());
        order.setLocation(location);
        
        return order;
    }

    private Courier mapToOpenApiModel(GetCouriersResponse response) {
        Courier courier = new Courier();
        courier.setId(response.getId());
        courier.setName(response.getName());
        
        Location location = new Location();
        location.setX(response.getLocation().getX());
        location.setY(response.getLocation().getY());
        courier.setLocation(location);
        
        return courier;
    }
}
