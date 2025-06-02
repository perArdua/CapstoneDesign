package com.example.campusin.common.config.swagger;

/**
 * Created by kok8454@gmail.com on 2023-05-13
 * Github : http://github.com/perArdua
 */

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "CampusIn API", version = "1.0.0", description = "CampusIn API 명세서입니다."),
        security = @SecurityRequirement(name = "Authorization")
)
@SecurityScheme(
        name = "Authorization",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class SwaggerConfig implements WebMvcConfigurer {

    @Getter
    @Setter
    static class Page {
        @Schema(description = "페이지 번호")
        private Integer page;

        @Schema(description = "페이지 크기")
        private Integer size;

        @Schema(description = "정렬(사용법: 컬럼명,ASC|DESC) : 띄어쓰기금지", example = "createdAt,ASC")
        private List<String> sort;
    }
}