package delivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import delivery.core.domain.model.Location;

@SpringBootApplication
public class DeliveryApplication {

	public static void main(String[] args) {
        SpringApplication.run(DeliveryApplication.class, args);
    }
}
