package org.vision.github.springboot.mqserver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author ganminghui
 * @date 2017/12/20
 */
@Component @ConfigurationProperties(prefix = "ext")
@Data public class ExtConfiguration {
    private String appName;
}