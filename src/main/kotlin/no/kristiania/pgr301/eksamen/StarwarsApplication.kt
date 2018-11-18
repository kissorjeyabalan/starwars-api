package no.kristiania.pgr301.eksamen

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.support.AbstractApplicationContext
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

@EnableSwagger2
@SpringBootApplication
class StarwarsApplication {
    @Bean
    fun swaggerApi(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("no.kristiania.pgr301.eksamen.controller"))
                .paths(PathSelectors.any())
                .build()
    }

    private fun apiInfo(): ApiInfo {
        return ApiInfoBuilder()
                .title("StarWars API")
                .description("API with useless details")
                .version("1")
                .build()
    }

    @Bean
    fun forwardToIndex() = object: WebMvcConfigurer {
        override fun addViewControllers(registry: ViewControllerRegistry) {
            registry.addRedirectViewController("/", "/swagger-ui.html")
        }
    }
}

fun main(args: Array<String>) {
    runApplication<StarwarsApplication>(*args)
}
