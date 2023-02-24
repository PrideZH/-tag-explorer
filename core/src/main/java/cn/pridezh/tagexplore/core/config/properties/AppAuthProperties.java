package cn.pridezh.tagexplore.core.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author PrideZH
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.auth")
public class AppAuthProperties {

    private String password;

}
