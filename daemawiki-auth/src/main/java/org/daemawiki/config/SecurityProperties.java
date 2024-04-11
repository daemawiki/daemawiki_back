package org.daemawiki.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("daemawiki.security")
public class SecurityProperties {
    private String secret;
    private String issuer;
    private Integer expiration;

}
