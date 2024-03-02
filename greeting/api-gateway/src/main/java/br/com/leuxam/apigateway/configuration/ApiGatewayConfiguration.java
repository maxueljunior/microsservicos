package br.com.leuxam.apigateway.configuration;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGatewayConfiguration {
	
	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
			.route("path_route", r -> r.path("/get")
				.filters(f ->
					f.addRequestHeader("Hellow", "World")
						.addRequestParameter("Hellow", "World")
					)
				.uri("http://httpbin.org"))
			.route(r -> r.path("/cambio-service/**")
					.uri("lb://cambio-service"))
			.route(r -> r.path("/book-service/**")
					.uri("lb://book-service"))
			.build();
	}
}
