package delivery.core.application.queries.getorders;

import delivery.core.domain.model.order.Order;
import delivery.core.domain.model.order.Status;
import delivery.core.ports.OrderRepository;
import libs.errs.Error;
import libs.errs.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public final class GetOrdersQueryHandlerImpl implements GetOrdersQueryHandler {
    private final OrderRepository orderRepository;

    public GetOrdersQueryHandlerImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Result<List<GetOrdersResponse>, Error> handle(GetOrdersQuery query) {
        List<Order> assignedOrders = orderRepository.findAllByStatus(Status.ASSIGNED);
        List<Order> createdOrders = orderRepository.findAllByStatus(Status.CREATED);
        assignedOrders.addAll(createdOrders);

        List<GetOrdersResponse> response = assignedOrders.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return Result.success(response);
    }

    private GetOrdersResponse mapToDto(Order order) {
        return new GetOrdersResponse(
                order.getId(),
                order.getLocation()
        );
    }
}
