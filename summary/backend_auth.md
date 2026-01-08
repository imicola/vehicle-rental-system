# 认证模块

## 范围
用户的登录与注册功能；密码哈希；JWT 签发。主要组成：
- 控制器： [src/main/java/com/java_db/demo/controller/AuthController.java](src/main/java/com/java_db/demo/controller/AuthController.java)
- 服务： [src/main/java/com/java_db/demo/service/AuthService.java](src/main/java/com/java_db/demo/service/AuthService.java)、[src/main/java/com/java_db/demo/service/UserService.java](src/main/java/com/java_db/demo/service/UserService.java)
- 仓库： [src/main/java/com/java_db/demo/repository/UserRepository.java](src/main/java/com/java_db/demo/repository/UserRepository.java)
- 实体/DTO： [src/main/java/com/java_db/demo/entity/User.java](src/main/java/com/java_db/demo/entity/User.java)、[src/main/java/com/java_db/demo/dto/LoginRequest.java](src/main/java/com/java_db/demo/dto/LoginRequest.java)、[src/main/java/com/java_db/demo/dto/LoginResponse.java](src/main/java/com/java_db/demo/dto/LoginResponse.java)、[src/main/java/com/java_db/demo/dto/RegisterDTO.java](src/main/java/com/java_db/demo/dto/RegisterDTO.java)
- 工具类： [src/main/java/com/java_db/demo/util/JwtProvider.java](src/main/java/com/java_db/demo/util/JwtProvider.java)

## API 概览
- `POST /api/auth/login`：使用用户名和密码，返回 JWT Token 及用户信息（包含 `role`）。
- `POST /api/auth/register`：注册新用户，默认 `role=0`；校验用户名唯一性及手机号（可选）唯一性。

## 处理流程
- 密码采用 BCrypt 加密（在 `SecurityConfig` 中配置 `PasswordEncoder` Bean）。
- 登录时根据用户名查用户并校验密码，成功后使用 `JwtProvider` 签发 Token，Token 的 `sub` 为用户 ID，并包含 `role` 声明，过期时间可通过 `jwt.expiration` 配置。
- 注册时检查用户名/手机号是否已被占用，使用 BCrypt 对密码加密后保存，默认分配普通客户角色（0）。
- `UserService` 提供按 ID/用户名查询、修改密码和更新个人信息等方法，但控制器中仅在认证相关场景使用。

## 主要风险与不足
- 缺少认证过滤器与授权规则：尽管能签发 JWT，但项目未在请求链中验证 Token，因此接口实际并未被保护。
- 未实现刷新令牌、令牌撤销或密码重置等机制。
- 控制器缺乏基于角色的校验；且许多接口接受客户端传入的 `userId`，存在越权风险。
- 项目有参数校验和校验消息，但缺少统一的全局异常处理器，错误会以默认的 400/500 响应抛回。

## 快速建议
- 在非开发环境中通过环境变量或机密管理服务覆盖 `jwt.secret`。
- 增加 HTTP 安全配置并注册 JWT 验证过滤器，使签发的 Token 能发挥实际保护作用。
- 考虑提供基于 Token 的 `GET /me` 接口，避免客户端传入 `userId` 导致的安全问题。
