package delivery.adapters.out.postgres;

import delivery.core.ports.UnitOfWork;
import libs.errs.Error;
import libs.errs.UnitResult;
import org.springframework.stereotype.Component;

@Component
public class UnitOfWorkImpl implements UnitOfWork {
    
    @Override
    public UnitResult<Error> commit() {
        return UnitResult.success();
    }
}
