package delivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "delivery")
@EntityScan(basePackages = "delivery")
@EnableScheduling
public class DeliveryApplication {

	public static void main(String[] args) {
        SpringApplication.run(DeliveryApplication.class, args);
    }
}
