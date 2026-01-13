package delivery.core.application.commands.createcourier;

import delivery.core.domain.model.Location;
import delivery.core.domain.model.courier.Courier;
import delivery.core.ports.CourierRepository;
import delivery.core.ports.UnitOfWork;
import libs.errs.Error;
import libs.errs.UnitResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public final class CreateCourierCommandHandlerImpl implements CreateCourierCommandHandler {
    private final CourierRepository courierRepository;
    private final UnitOfWork unitOfWork;

    public CreateCourierCommandHandlerImpl(CourierRepository courierRepository, UnitOfWork unitOfWork) {
        this.courierRepository = courierRepository;
        this.unitOfWork = unitOfWork;
    }

    @Override
    @Transactional
    public UnitResult<Error> handle(CreateCourierCommand command) {
        Location location = Location.createRandom();

        var courierResult = Courier.create(command.getName(), command.getSpeed(), location);
        if (courierResult.isFailure()) {
            return UnitResult.failure(courierResult.getError());
        }

        Courier courier = courierResult.getValue();
        courierRepository.save(courier);
        unitOfWork.commit();

        return UnitResult.success();
    }
}
