package delivery.core.application.queries.getcouriers;

import delivery.core.domain.model.courier.Courier;
import delivery.core.ports.CourierRepository;
import libs.errs.Error;
import libs.errs.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public final class GetCouriersQueryHandlerImpl implements GetCouriersQueryHandler {
    private final CourierRepository courierRepository;

    public GetCouriersQueryHandlerImpl(CourierRepository courierRepository) {
        this.courierRepository = courierRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Result<List<GetCouriersResponse>, Error> handle(GetCouriersQuery query) {
        List<Courier> couriers = courierRepository.findAll();
        
        List<GetCouriersResponse> response = couriers.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return Result.success(response);
    }

    private GetCouriersResponse mapToDto(Courier courier) {
        return new GetCouriersResponse(
                courier.getId(),
                courier.getName(),
                courier.getLocation()
        );
    }
}
