package com.skkudteam3.skkusirenorder.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@OpenAPIDefinition(
        info = @Info(
                title = "SKKU-NYAM 프로젝트 API 명세서",
                description = "skku-nyam 프로젝트 개발용 API 명세서입니다.",
                version = "v1"
        )
)
@Configuration
public class SwaggerConfig {

}