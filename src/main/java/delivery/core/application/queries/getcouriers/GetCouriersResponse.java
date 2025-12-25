package delivery.core.application.queries.getcouriers;

import delivery.core.domain.model.Location;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class GetCouriersResponse {
    private final UUID id;
    private final String name;
    private final Location location;
}
