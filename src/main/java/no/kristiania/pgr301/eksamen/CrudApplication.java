package no.kristiania.pgr301.eksamen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

//@EnableSwagger2
@SpringBootApplication
@EnableJpaRepositories
@EntityScan(basePackages = "no.kristiania.pgr301.eksamen.entity")
public class CrudApplication {

	public static void main(String[] args) {
		Logger logger = LoggerFactory.getLogger(CrudApplication.class);
		logger.info("Starting CrudApplication...");
		SpringApplication.run(CrudApplication.class, args);
		logger.info("CrudApplication started!");
	}

	/*@Bean
    public Docket swaggerApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("no.kristiania.pgr301.eksamen.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Vehicles API")
                .description("Useless API with useless details")
                .version("1")
                .build();
    }

    @Bean
    public WebMvcConfigurer forwardToIndex() {
        return new WebMvcConfigurer() {
            @Override
            public void addViewControllers(ViewControllerRegistry registry) {
                registry.addRedirectViewController("/", "/swagger-ui.html");
            }
        };
    }*/
}
