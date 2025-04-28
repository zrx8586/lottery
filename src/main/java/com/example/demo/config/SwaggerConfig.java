package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger 配置类，用于生成 OpenAPI 文档。
 * 通过配置，可以为项目提供 API 文档的展示和测试功能。
 */
@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    /**
     * 配置 OpenAPI 文档的基本信息和安全设置。
     *
     * @return OpenAPI 对象，包含文档的元信息和安全配置。
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // 配置文档的基本信息
                .info(new Info()
                        .title("API Documentation") // 文档标题
                        .version("1.0") // 文档版本
                        .description("This is the API documentation for the project.")) // 文档描述
                // 添加安全项，指定需要使用的认证方式
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                // 配置安全方案
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, // 定义安全方案的名称
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP) // 认证类型为 HTTP
                                        .scheme("bearer") // 使用 Bearer Token
                                        .bearerFormat("JWT"))); // 指定 Token 格式为 JWT
    }
}