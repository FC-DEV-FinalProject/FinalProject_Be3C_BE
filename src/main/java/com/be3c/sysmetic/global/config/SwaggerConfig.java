package com.be3c.sysmetic.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        /*
        JWT 인증 스키마 설정
        API 문서에 보안 설정 추가되어 API 요청시 JWT 토큰을 헤더에 포함시키도록 할 수 있다.
         */
        String jwt = "JWT";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);
        Components components = new Components().addSecuritySchemes(jwt, new SecurityScheme()
                .name(jwt)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT"));


        // 서버 URL 설정
        Server httpsServer = new Server();
        httpsServer.setUrl("https://3.39.211.112.nip.io");
        httpsServer.setDescription("Sysmetic https 서버입니다.");

        Server httpServer = new Server();
        httpServer.setUrl("http://3.39.211.112.nip.io");
        httpServer.setDescription("Sysmetic http 서버입니다.");

        Server localServer = new Server();
        localServer.setUrl("http://localhost:8080");
        localServer.setDescription("Sysmetic local 서버입니다.");


        // Components 객체 - 보안 스키마, 파라미터, 응답 등 정의
        return new OpenAPI()
                .components(new Components())
                .components(components)
                .addSecurityItem(securityRequirement)
                .servers(List.of(httpsServer, httpServer, localServer))
                .info(apiInfo());
    }

    // API문서의 제목, 설명, 버전 등의 기본 정보 설정
    private Info apiInfo() {
        return new Info()
                .title("Be3C API")
                .description("Be3C Swagger API Document")
                .version("1.0");
    }
}