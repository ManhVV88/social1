package com.example.social.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.annotations.enums.*;

//import io.swagger.v3.oas.models.Components;
//import io.swagger.v3.oas.models.OpenAPI;
//import io.swagger.v3.oas.models.info.Contact;
//import io.swagger.v3.oas.models.info.Info;
//import io.swagger.v3.oas.models.info.License;
//import io.swagger.v3.oas.models.security.SecurityRequirement;
//import io.swagger.v3.oas.models.security.SecurityScheme;
//import springfox.documentation.builders.ApiInfoBuilder;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.service.ApiInfo;
//import springfox.documentation.service.ApiKey;
//import springfox.documentation.service.AuthorizationScope;
//import springfox.documentation.service.SecurityReference;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spi.service.contexts.SecurityContext;
//import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;



@Configuration
//@EnableSwagger2
@OpenAPIDefinition(info = @Info(title = "Social API", version = "1.0", description = "Social API",
contact = @Contact(name = "Vu Van Manh", email = "vanmanhvn@gmail.com")),
security = {@SecurityRequirement(name = "basicAuth"), @SecurityRequirement(name = "bearerToken")})
@SecuritySchemes({
@SecurityScheme(name = "bearerToken", type = SecuritySchemeType.HTTP, scheme = "Bearer", bearerFormat = "JWT")
})
public class SwaggerConfig {

//	@Bean
//    public Docket api() { 
//        return new Docket(DocumentationType.SWAGGER_2)  
//          .select()                                  
//          .apis(RequestHandlerSelectors.basePackage("com.example.social.controller"))              
//          .paths(PathSelectors.any())                          
//          .build()
//          .securitySchemes(Arrays.asList(apiKeySecurityScheme()))
//          .securityContexts(Arrays.asList(securityContext()))
//          .apiInfo(apiInfo());                                           
//    }
//    
//    private ApiInfo apiInfo() {
//        return new ApiInfoBuilder()
//                .title("API Documentation")
//                .description("API Documentation for your project")
//                .version("1.0.0")
//                .build()
//                ;
//    }    
//    
//    private ApiKey apiKeySecurityScheme() {
//        return new ApiKey("Bearer", "Authorization", "header");
//    }
//	
//    
//	@SuppressWarnings("deprecation")
//	private SecurityContext securityContext() {
//        return SecurityContext.builder().securityReferences(defaultAuth())
//            .forPaths(PathSelectors.any()).build();
//    }
//
//    private List<SecurityReference> defaultAuth() {
//        AuthorizationScope authorizationScope = new AuthorizationScope(
//            "global", "accessEverything");
//        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
//        authorizationScopes[0] = authorizationScope;
//        return Arrays.asList(new SecurityReference("Bearer",
//            authorizationScopes));
//        }

    
}