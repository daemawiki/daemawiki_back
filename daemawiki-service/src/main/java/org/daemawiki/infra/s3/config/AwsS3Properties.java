package org.daemawiki.infra.s3.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "aws")
public class AwsS3Properties {

    private String access;
    private String secret;
    private String bucket;
    private String region;

}
