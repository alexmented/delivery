package delivery.core.application.commands.movecouriers;

import libs.errs.Error;
import libs.errs.UnitResult;

public interface MoveCouriersCommandHandler {
    UnitResult<Error> handle(MoveCouriersCommand command);
}
