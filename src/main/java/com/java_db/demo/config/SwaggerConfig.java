package com.java_db.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger/OpenAPI 配置类
 * 配置 API 文档的基本信息
 */
@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("车辆租赁系统 API")
                        .version("1.0.0")
                        .description("车辆租赁管理系统的 RESTful API 文档\n\n" +
                                "## 功能模块\n" +
                                "- **认证模块**: 用户登录、注册\n" +
                                "- **车辆模块**: 车辆查询、管理\n" +
                                "- **订单模块**: 订单创建、查询、还车\n" +
                                "- **支付模块**: 押金、尾款、罚金管理\n" +
                                "- **门店模块**: 门店查询、管理\n" +
                                "- **维修模块**: 车辆维修记录管理\n\n" +
                                "## 技术栈\n" +
                                "- Java 21 + Spring Boot 4.0.1\n" +
                                "- PostgreSQL 18.1\n" +
                                "- JWT 认证\n" +
                                "- BCrypt 密码加密")
                        .contact(new Contact()
                                .name("开发团队")
                                .email("support@carrental.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("本地开发环境"),
                        new Server()
                                .url("https://api.carrental.com")
                                .description("生产环境（示例）")
                ));
    }
}
