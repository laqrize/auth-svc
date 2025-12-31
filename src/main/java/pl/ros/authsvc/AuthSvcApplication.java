package pl.ros.authsvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import pl.ros.commons.config.CommonsAutoConfiguration;

@SpringBootApplication
//@Import(CommonsAutoConfiguration.class)
public class AuthSvcApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthSvcApplication.class, args);
    }

}
