package org.elis.progettoing.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.ResourceChainRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import static org.mockito.Mockito.*;

class WebConfigTest {

    @Test
    void addResourceHandlers_shouldAddResourceHandlerForImages() {
        ResourceHandlerRegistry registry = mock(ResourceHandlerRegistry.class);
        ResourceHandlerRegistration registration = mock(ResourceHandlerRegistration.class);
        ResourceChainRegistration chainRegistration = mock(ResourceChainRegistration.class);

        when(registry.addResourceHandler("/images/**")).thenReturn(registration);
        when(registration.addResourceLocations("file:storage/images/")).thenReturn(registration);
        when(registration.setCachePeriod(3600)).thenReturn(registration);
        when(registration.resourceChain(true)).thenReturn(chainRegistration);

        WebConfig webConfig = new WebConfig();
        webConfig.addResourceHandlers(registry);

        verify(registry, times(1)).addResourceHandler("/images/**");
        verify(registration, times(1)).addResourceLocations("file:storage/images/");
        verify(registration, times(1)).setCachePeriod(3600);
        verify(registration, times(1)).resourceChain(true);
    }
}