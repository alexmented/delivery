package delivery.core.application.queries.getcouriers;

import libs.errs.Result;
import libs.errs.Error;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GetCouriersQuery {
    public static Result<GetCouriersQuery, Error> create() {
        return Result.success(new GetCouriersQuery());
    }
}
