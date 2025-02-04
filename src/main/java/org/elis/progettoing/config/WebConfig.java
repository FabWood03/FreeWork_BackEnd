package org.elis.progettoing.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configures the web application.
 * <p>
 * This class configures the web application by adding resource handlers for serving static resources
 * (images) and enabling Spring Data web support for pagination.
 * </p>
 */
@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class WebConfig implements WebMvcConfigurer {
    private static final String STORAGE_LOCATION = "file:storage/images/";

    /**
     * Adds a resource handler for serving static resources (images).
     *
     * @param registry the resource handler registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations(STORAGE_LOCATION)
                .setCachePeriod(3600)
                .resourceChain(true);
    }}

