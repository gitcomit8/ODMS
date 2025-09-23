package in.srmup.odms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OdManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(OdManagementSystemApplication.class, args);
    }

}
