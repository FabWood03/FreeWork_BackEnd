package org.elis.progettoing.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Configuration class for enabling asynchronous processing.
 * <p>
 * This class enables asynchronous processing for the application.
 * </p>
 */
@Configuration
@EnableAsync(proxyTargetClass = true)
public class AsyncConfig {
}
