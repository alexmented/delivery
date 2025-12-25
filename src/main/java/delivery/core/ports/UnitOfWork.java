package delivery.core.ports;

import libs.errs.Error;
import libs.errs.UnitResult;

public interface UnitOfWork {
    UnitResult<Error> commit();
}
