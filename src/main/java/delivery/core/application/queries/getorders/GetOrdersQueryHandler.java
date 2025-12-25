package delivery.core.application.queries.getorders;

import libs.errs.Error;
import libs.errs.Result;
import java.util.List;

public interface GetOrdersQueryHandler {
    Result<List<GetOrdersResponse>, Error> handle(GetOrdersQuery query);
}
