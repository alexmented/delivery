package delivery.core.application.queries.getorders;

import libs.errs.Result;
import libs.errs.Error;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GetOrdersQuery {
    public static Result<GetOrdersQuery, Error> create() {
        return Result.success(new GetOrdersQuery());
    }
}
