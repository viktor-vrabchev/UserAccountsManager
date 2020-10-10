package com.westnacher.uam.configurations;

import java.util.function.Predicate;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class SwaggerUIConfigurations {

	private static final String CONTACT_URL = "https://www.linkedin.com/in/viktor-vrabchev-16a047160/";
	private static final String CONTACT_EMAIL = "viktorvrabchev@gmail.com";
	private static final String CONTACT_NAME = "Viktor Vrabchev";
	private static final String API_INFO_TITLE = "User Accounts Manager (UAM)";
	private static final String API_INFO_DESCRIPTION = "Projects for managing user accounts - list , add, edit, delete, create";
	private static final String API_INFO_VERSION = "1.0";

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(apiInfo())
				.select()
				.paths(PathSelectors.regex("^.*/users.*$"))
				.build();
	}
	
	private ApiInfo apiInfo() {
		Contact contact = new Contact(CONTACT_NAME, CONTACT_URL, CONTACT_EMAIL);
        return new ApiInfoBuilder()
                .title(API_INFO_TITLE)
                .description(API_INFO_DESCRIPTION)
                .contact(contact)
                .version(API_INFO_VERSION)
                .build();
    }

}
