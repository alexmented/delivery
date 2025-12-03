package delivery.core.application.commands;

import libs.errs.Error;
import libs.errs.UnitResult;

public interface DispatchOrderCommandHandler {
    UnitResult<Error> handle(DispatchOrderCommand command);
}
