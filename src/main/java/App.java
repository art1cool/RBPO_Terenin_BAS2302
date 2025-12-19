import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(scanBasePackages = {"controller", "service", "entity", "repository", "configuration", "util"})
@EnableJpaRepositories("repository")
@EntityScan("entity")
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}