package cn.pridezh.tagexplore.core.config.properties;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author PrideZH
 */
@Data
@RequiredArgsConstructor
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private String repository;

    private String database;

    private final AppAuthProperties auth;

}
