package delivery.adapters.out.postgres;

import delivery.core.domain.model.courier.Courier;
import delivery.core.ports.CourierRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CourierRepositoryImpl implements CourierRepository {

    private final CourierJpaRepository jpa;

    public CourierRepositoryImpl(CourierJpaRepository jpa) {
        this.jpa = jpa;
    }

    public void save(Courier courier) {
        jpa.save(courier);
    }

    public Optional<Courier> findById(UUID id) {
        return jpa.findById(id);
    }

    @Override
    public List<Courier> findAllFree() {
        return jpa.findAllFree();
    }
}
