# gateway-service - Claude Code 工作目录

## 文档位置

- **Epic文档**: `.claude/docs/epic.md`
- **Git工作流**: `.claude/docs/git-workflow.md`
- **Spring Boot模板**: `.claude/docs/spring-boot-template.md` (仅Java服务)

## 相关Epic

查看 `.claude/docs/epic.md` 了解：
- 业务需求
- 技术方案
- 任务分解
- 工作量估算

## 开发指南

1. 阅读Epic文档了解需求
2. 遵循Git工作流规范提交代码
3. 使用Spring Boot模板创建项目结构（Java服务）
4. 参考 hkd-common 库的公共组件

## 数据库连接

- **PostgreSQL**: localhost:5432
- **用户名**: hkd_admin
- **密码**: hkd_dev_password_2024
- **Redis**: localhost:6379 / hkd_redis_2024

## 通用库

```xml
<dependency>
    <groupId>com.hkd</groupId>
    <artifactId>hkd-common</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

---

Generated with Claude Code
