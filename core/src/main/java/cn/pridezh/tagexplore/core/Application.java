package cn.pridezh.tagexplore.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

/**
 * @author PrideZH
 */
@Slf4j
@ComponentScan(basePackages = "cn.pridezh.tagexplore")
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(Application.class, args);

        Environment env = applicationContext.getEnvironment();
        String port = env.getProperty("server.port");

        log.info("Console: http://localhost:{}", port);
    }

}

