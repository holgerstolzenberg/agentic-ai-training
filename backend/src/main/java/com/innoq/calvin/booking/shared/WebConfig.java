package com.innoq.calvin.booking.shared;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Value("${cors.allowed-origins:http://localhost:4200}")
	private String allowedOriginsPattern;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOriginPatterns(allowedOriginsPattern)
				.allowedMethods("GET", "POST", "DELETE", "PATCH", "OPTIONS").allowedHeaders("*");
	}
}
