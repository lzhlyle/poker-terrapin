package com.lzhlyle.poker.terrapin.config;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class WebSiteSwagger {
    @Bean
    public Docket createRestApi() {
        // 表示扫描的包路径，可以随意指定
        Predicate<RequestHandler> api = RequestHandlerSelectors.basePackage("com.lzhlyle.poker.terrapin.web.api");
        Predicate<RequestHandler> common = RequestHandlerSelectors.basePackage("com.lzhlyle.poker.terrapin.web.controller");

        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select().apis(Predicates.or(api, common))
                .paths(PathSelectors.any()).build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("springboot利用swagger构建api文档").version("1.0").build();
    }
}
