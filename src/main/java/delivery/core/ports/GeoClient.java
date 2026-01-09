package delivery.core.ports;

import delivery.core.domain.model.Location;
import libs.errs.Error;
import libs.errs.Result;

public interface GeoClient {
    Result<Location, Error> getGeolocation(String street);
}
