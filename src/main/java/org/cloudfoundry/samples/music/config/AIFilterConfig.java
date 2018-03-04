package org.cloudfoundry.samples.music.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.microsoft.applicationinsights.web.internal.WebRequestTrackingFilter;
import javax.servlet.Filter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//@Profile("ai")
public class AIFilterConfig {

    private static final Log logger = LogFactory.getLog(AIFilterConfig.class);

    @Value("${spring.application.name}")
    private String appName;
   
    @Bean
    public FilterRegistrationBean someFilterRegistration() {
        logger.info("ApplicationInsights!! - loading filetr");
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(ApplicationInsightsWebFilter());
        registration.addUrlPatterns("/*");
       // registration.addInitParameter("paramName", "paramValue");
        registration.setName("ApplicationInsightsWebFilter");
        registration.setOrder(1);
        return registration;
    } 

    @Bean
    public Filter ApplicationInsightsWebFilter() {
        return new WebRequestTrackingFilter(appName);
    }

}
