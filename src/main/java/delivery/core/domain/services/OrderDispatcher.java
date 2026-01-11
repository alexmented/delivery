package delivery.core.domain.services;

import delivery.core.domain.model.courier.Courier;
import delivery.core.domain.model.order.Order;
import libs.errs.Error;
import libs.errs.Result;

import java.util.List;

public interface OrderDispatcher {
    Result<Courier, Error> dispatch(Order order, List<Courier> couriers);
}
