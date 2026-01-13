package delivery.core.application.queries.getcouriers;

import libs.errs.Error;
import libs.errs.Result;
import java.util.List;

public interface GetCouriersQueryHandler {
    Result<List<GetCouriersResponse>, Error> handle(GetCouriersQuery query);
}
