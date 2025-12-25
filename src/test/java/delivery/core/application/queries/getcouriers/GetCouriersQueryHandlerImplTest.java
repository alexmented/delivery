package delivery.core.application.queries.getcouriers;

import delivery.core.domain.model.Location;
import delivery.core.domain.model.courier.Courier;
import delivery.core.ports.CourierRepository;
import delivery.core.ports.UnitOfWork;
import jakarta.transaction.Transactional;
import libs.errs.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Transactional
class GetCouriersQueryHandlerImplTest {

    @Container
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15.3")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void configureDataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private ApplicationContext context;

    @Test
    void shouldReturnListOfCouriers() {
        var unitOfWork = context.getBean(UnitOfWork.class);
        var courierRepository = context.getBean(CourierRepository.class);
        var handler = context.getBean(GetCouriersQueryHandler.class);

        Location location = Location.create(1, 1).getValue();
        Courier courier = Courier.create("Test Courier", 10, location).getValue();
        
        courierRepository.save(courier);
        unitOfWork.commit();

        GetCouriersQuery query = GetCouriersQuery.create().getValue();

        Result<List<GetCouriersResponse>, libs.errs.Error> result = handler.handle(query);

        assertThat(result.isSuccess()).isTrue();
        List<GetCouriersResponse> responses = result.getValue();
        
        boolean found = responses.stream()
                .anyMatch(r -> r.getId().equals(courier.getId()) && 
                               r.getName().equals(courier.getName()));
        assertThat(found).isTrue();
    }
}
