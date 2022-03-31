package backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@EntityScan("backend.model")
@EnableJpaRepositories("backend.repository")
public class G2Application {

    public static void main(String[] args) {
        SpringApplication.run(G2Application.class, args);
    }
}
