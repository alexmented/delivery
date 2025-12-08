package delivery.adapters.out.postgres;

import delivery.core.domain.model.courier.Courier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface CourierJpaRepository extends JpaRepository<Courier, UUID> {
    @Query("SELECT c FROM Courier c WHERE NOT EXISTS (SELECT sp FROM c.storagePlaces sp WHERE sp.orderId IS NOT NULL)")
    List<Courier> findAllFree();
}
