package delivery.core.application.queries.getorders;

import delivery.core.domain.model.Location;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class GetOrdersResponse {
    private final UUID id;
    private final Location location;
}
