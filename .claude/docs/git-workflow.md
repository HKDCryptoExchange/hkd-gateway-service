# HKD Exchange - Git 工作流规范

本文档定义HKD Exchange项目的Git工作流程和代码提交规范。

---

## 分支策略

### 主要分支

```
main (production)
  └── develop (integration)
       ├── feature/* (功能开发)
       ├── bugfix/* (Bug修复)
       ├── hotfix/* (紧急修复)
       └── release/* (发布准备)
```

### 分支说明

#### 1. `main` 分支
- **用途**: 生产环境代码
- **保护**: 仅通过PR合并，需要Review
- **标签**: 每次合并后打版本标签（v1.0.0, v1.1.0等）
- **禁止**: 直接push

#### 2. `develop` 分支
- **用途**: 开发集成分支
- **保护**: 需要PR + Review
- **来源**: feature/bugfix分支合并
- **禁止**: 直接commit（除紧急情况）

#### 3. `feature/*` 分支
- **命名**: `feature/<task-id>-<short-desc>`
- **示例**: `feature/HKD-123-user-registration`
- **来源**: 从develop分支创建
- **合并**: PR到develop分支
- **生命周期**: 完成后删除

#### 4. `bugfix/*` 分支
- **命名**: `bugfix/<task-id>-<short-desc>`
- **示例**: `bugfix/HKD-456-login-error`
- **来源**: 从develop分支创建
- **合并**: PR到develop分支
- **生命周期**: 完成后删除

#### 5. `hotfix/*` 分支
- **命名**: `hotfix/<task-id>-<short-desc>`
- **示例**: `hotfix/HKD-789-security-patch`
- **来源**: 从main分支创建
- **合并**: PR到main和develop分支
- **生命周期**: 完成后删除

#### 6. `release/*` 分支
- **命名**: `release/v<version>`
- **示例**: `release/v1.0.0`
- **来源**: 从develop分支创建
- **用途**: 发布前的最后准备（版本号、文档等）
- **合并**: PR到main分支，并打tag
- **生命周期**: 发布后删除

---

## 提交消息规范

### Conventional Commits 格式

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Type 类型

| Type | 描述 | 示例 |
|------|------|------|
| `feat` | 新功能 | `feat(user): add user registration` |
| `fix` | Bug修复 | `fix(auth): resolve token expiration issue` |
| `docs` | 文档更新 | `docs(readme): update installation guide` |
| `style` | 代码格式（不影响代码运行） | `style(user): format code with prettier` |
| `refactor` | 重构（既非新增功能也非Bug修复） | `refactor(wallet): optimize balance query` |
| `perf` | 性能优化 | `perf(trading): improve matching speed` |
| `test` | 测试相关 | `test(order): add integration tests` |
| `build` | 构建系统或外部依赖 | `build(deps): upgrade spring boot to 3.2.1` |
| `ci` | CI/CD配置 | `ci(github): add automated testing workflow` |
| `chore` | 其他杂项 | `chore(git): update .gitignore` |

### Scope 范围

按业务域或微服务命名:

| Scope | 描述 |
|-------|------|
| `user` | 用户服务 |
| `kyc` | KYC服务 |
| `auth` | 认证服务 |
| `account` | 账户服务 |
| `wallet` | 钱包服务 |
| `deposit` | 充值服务 |
| `withdraw` | 提现服务 |
| `asset` | 资产服务 |
| `trading` | 交易服务 |
| `order` | 订单服务 |
| `matching` | 撮合引擎 |
| `settlement` | 清算服务 |
| `market` | 行情服务 |
| `risk` | 风控服务 |
| `ml` | 机器学习服务 |
| `gateway` | 网关服务 |
| `notify` | 通知服务 |
| `admin` | 管理后台 |
| `web` | 前端Web |
| `common` | 通用库 |

### Subject 主题

- 使用祈使句，现在时：`add` 而非 `added` 或 `adds`
- 首字母小写
- 不以句号结尾
- 限制在50字符以内

### Body 正文（可选）

- 详细描述改动内容
- 说明改动的原因和影响
- 每行限制72字符

### Footer 页脚（可选）

- 关联Issue: `Closes #123, #456`
- 破坏性变更: `BREAKING CHANGE: ...`
- Claude Code标识:
  ```
  Generated with [Claude Code](https://claude.com/claude-code)

  Co-Authored-By: Claude <noreply@anthropic.com>
  ```

### 完整示例

```
feat(user): implement user registration with email verification

Add user registration endpoint with following features:
- Email/password validation
- BCrypt password hashing (strength 12)
- Email verification token generation
- Send verification email via notify-service

Related to business requirement in epic.md section 2.1

Closes #123

Generated with [Claude Code](https://claude.com/claude-code)

Co-Authored-By: Claude <noreply@anthropic.com>
```

---

## Pull Request 工作流

### 1. 创建Feature分支

```bash
# 从develop分支创建新分支
git checkout develop
git pull origin develop
git checkout -b feature/HKD-123-user-registration
```

### 2. 开发并提交

```bash
# 提交代码（使用规范的commit message）
git add .
git commit -m "feat(user): add user registration endpoint"

# 多次提交...
git commit -m "test(user): add unit tests for registration"
git commit -m "docs(user): update API documentation"
```

### 3. 推送到远程

```bash
# 首次推送
git push -u origin feature/HKD-123-user-registration

# 后续推送
git push
```

### 4. 创建Pull Request

**PR标题格式**:
```
[HKD-123] feat(user): implement user registration
```

**PR描述模板**:
```markdown
## Summary
简述本PR的内容（1-3个要点）

## Changes
- 新增user registration API
- 实现email验证流程
- 添加单元测试和集成测试

## Test Plan
- [ ] 单元测试通过（覆盖率 > 80%）
- [ ] 集成测试通过
- [ ] 手动测试注册流程
- [ ] 邮件发送功能正常

## Related Issues
Closes #123

## Screenshots (if applicable)
（如果是UI相关，附上截图）

---
Generated with [Claude Code](https://claude.com/claude-code)
```

### 5. Code Review

**Reviewer清单**:
- [ ] 代码符合项目规范（Google Style Guide）
- [ ] 测试充分（单元+集成）
- [ ] 没有安全漏洞（SQL注入、XSS等）
- [ ] 性能符合要求
- [ ] 文档完整（API文档、注释）
- [ ] 没有硬编码密码或密钥
- [ ] Commit message符合规范

**Review流程**:
1. Instance 1（项目管理）负责Review所有跨域PR
2. 同域内PR可由同域开发者Review
3. 至少1个Approve才能合并
4. 重要变更需要2个Approve

### 6. 合并PR

```bash
# 方式1: Squash and Merge (推荐用于feature分支)
# GitHub UI选择"Squash and merge"

# 方式2: Create a merge commit (用于release分支)
# GitHub UI选择"Create a merge commit"

# 方式3: Rebase and merge (用于简单的bugfix)
# GitHub UI选择"Rebase and merge"
```

**合并策略选择**:
- **Feature分支**: Squash and merge（保持main/develop历史清晰）
- **Release分支**: Create a merge commit（保留完整历史）
- **Hotfix分支**: Squash and merge

### 7. 删除已合并分支

```bash
# 合并后自动删除远程分支（在GitHub设置中启用）
# 手动删除本地分支
git branch -d feature/HKD-123-user-registration
```

---

## 版本标签规范

### Semantic Versioning

格式: `v<major>.<minor>.<patch>`

- **Major**: 不兼容的API变更（如v1.0.0 → v2.0.0）
- **Minor**: 向后兼容的功能新增（如v1.0.0 → v1.1.0）
- **Patch**: 向后兼容的Bug修复（如v1.0.0 → v1.0.1）

### 创建标签

```bash
# 在main分支上创建标签
git checkout main
git pull origin main

# 创建带注释的标签
git tag -a v1.0.0 -m "Release v1.0.0: Initial production release"

# 推送标签到远程
git push origin v1.0.0

# 推送所有标签
git push origin --tags
```

### 预发布版本

格式: `v<major>.<minor>.<patch>-<pre-release>`

示例:
- `v1.0.0-alpha.1`: 内部测试版
- `v1.0.0-beta.1`: 公开测试版
- `v1.0.0-rc.1`: 候选发布版

---

## 常见场景

### 场景1: 开发新功能

```bash
# 1. 创建feature分支
git checkout develop
git pull origin develop
git checkout -b feature/HKD-123-implement-kyc

# 2. 开发并提交
# ... 编码 ...
git add .
git commit -m "feat(kyc): implement L1 KYC verification"

# 3. 推送并创建PR
git push -u origin feature/HKD-123-implement-kyc
# 在GitHub创建PR: develop ← feature/HKD-123-implement-kyc

# 4. Review通过后合并
# 在GitHub UI点击"Squash and merge"

# 5. 清理
git checkout develop
git pull origin develop
git branch -d feature/HKD-123-implement-kyc
```

### 场景2: 修复Bug

```bash
# 1. 创建bugfix分支
git checkout develop
git pull origin develop
git checkout -b bugfix/HKD-456-fix-login-error

# 2. 修复并提交
# ... 修复代码 ...
git add .
git commit -m "fix(auth): resolve JWT token validation error"

# 3. 推送并创建PR
git push -u origin bugfix/HKD-456-fix-login-error
# 在GitHub创建PR: develop ← bugfix/HKD-456-fix-login-error

# 4. 合并并清理
# ...（同场景1）
```

### 场景3: 紧急修复生产环境Bug

```bash
# 1. 从main创建hotfix分支
git checkout main
git pull origin main
git checkout -b hotfix/HKD-789-security-patch

# 2. 修复并提交
# ... 修复代码 ...
git add .
git commit -m "fix(security): patch SQL injection vulnerability"

# 3. 推送
git push -u origin hotfix/HKD-789-security-patch

# 4. 创建两个PR
# PR1: main ← hotfix/HKD-789-security-patch
# PR2: develop ← hotfix/HKD-789-security-patch

# 5. 合并到main后打紧急版本号
git checkout main
git pull origin main
git tag -a v1.0.1 -m "Hotfix v1.0.1: Security patch"
git push origin v1.0.1

# 6. 清理
git branch -d hotfix/HKD-789-security-patch
```

### 场景4: 准备发布

```bash
# 1. 从develop创建release分支
git checkout develop
git pull origin develop
git checkout -b release/v1.0.0

# 2. 更新版本号和文档
# 更新pom.xml、package.json等版本号
# 更新CHANGELOG.md

git add .
git commit -m "chore(release): prepare for v1.0.0"

# 3. 推送
git push -u origin release/v1.0.0

# 4. 创建PR: main ← release/v1.0.0

# 5. 合并后打标签
git checkout main
git pull origin main
git tag -a v1.0.0 -m "Release v1.0.0: Initial production release"
git push origin v1.0.0

# 6. 将release合并回develop
git checkout develop
git merge release/v1.0.0
git push origin develop

# 7. 清理
git branch -d release/v1.0.0
git push origin --delete release/v1.0.0
```

### 场景5: 同步develop最新变更

```bash
# 在feature分支中同步develop最新变更
git checkout feature/HKD-123-my-feature
git fetch origin
git rebase origin/develop

# 如果有冲突，解决后继续
git add <resolved-files>
git rebase --continue

# 强制推送（因为rebase改变了历史）
git push --force-with-lease
```

---

## Git配置建议

### 全局配置

```bash
# 用户信息
git config --global user.name "Your Name"
git config --global user.email "your.email@example.com"

# 默认分支名
git config --global init.defaultBranch main

# 编辑器
git config --global core.editor "vim"

# 彩色输出
git config --global color.ui auto

# 默认合并策略
git config --global merge.ff false
git config --global pull.rebase true

# 自动修剪已删除的远程分支
git config --global fetch.prune true
```

### 仓库配置

```bash
# 配置提交模板
git config commit.template .gitmessage
```

创建 `.gitmessage` 文件:
```
# <type>(<scope>): <subject>
# |<----  限制50字符  ---->|

# <body>
# |<----  每行限制72字符   ---->|

# <footer>

# Type: feat, fix, docs, style, refactor, perf, test, build, ci, chore
# Scope: user, kyc, auth, account, wallet, deposit, withdraw, asset,
#        trading, order, matching, settlement, market, risk, ml,
#        gateway, notify, admin, web, common
#
# Generated with [Claude Code](https://claude.com/claude-code)
# Co-Authored-By: Claude <noreply@anthropic.com>
```

---

## Git Hooks

### Pre-commit Hook

创建 `.git/hooks/pre-commit`:

```bash
#!/bin/bash

echo "Running pre-commit checks..."

# 1. 检查是否有文件包含敏感信息
if git diff --cached --name-only | xargs grep -E '(password|secret|api_key|private_key).*=.*["\047][^"\047]{8,}["\047]' 2>/dev/null; then
    echo "❌ Error: Potential sensitive information detected!"
    echo "Please remove passwords, secrets, or API keys from your code."
    exit 1
fi

# 2. Java代码格式检查（如果有修改的Java文件）
JAVA_FILES=$(git diff --cached --name-only --diff-filter=ACM | grep '\.java$')
if [ -n "$JAVA_FILES" ]; then
    echo "Checking Java code style..."
    # 这里可以集成checkstyle或google-java-format
fi

# 3. 运行单元测试（可选，可能较慢）
# mvn test

echo "✅ Pre-commit checks passed!"
exit 0
```

```bash
chmod +x .git/hooks/pre-commit
```

### Commit-msg Hook

创建 `.git/hooks/commit-msg`:

```bash
#!/bin/bash

COMMIT_MSG_FILE=$1
COMMIT_MSG=$(cat "$COMMIT_MSG_FILE")

# Conventional Commits格式验证
PATTERN="^(feat|fix|docs|style|refactor|perf|test|build|ci|chore)(\(.+\))?: .{1,50}$"

if ! echo "$COMMIT_MSG" | head -1 | grep -qE "$PATTERN"; then
    echo "❌ Error: Commit message does not follow Conventional Commits format!"
    echo ""
    echo "Format: <type>(<scope>): <subject>"
    echo ""
    echo "Example: feat(user): add user registration"
    echo ""
    echo "Types: feat, fix, docs, style, refactor, perf, test, build, ci, chore"
    exit 1
fi

echo "✅ Commit message format is valid!"
exit 0
```

```bash
chmod +x .git/hooks/commit-msg
```

---

## 最佳实践

### DO ✅

1. **小而频繁的提交**
   - 每个commit专注于一个改动
   - 便于code review
   - 便于回滚

2. **有意义的commit message**
   - 遵循Conventional Commits
   - 说明WHY而不是WHAT

3. **经常同步**
   - 每天至少pull一次develop
   - 避免长期feature分支

4. **Code review前自测**
   - 运行所有测试
   - 检查代码风格
   - 手动测试功能

5. **保持分支整洁**
   - 及时删除已合并的分支
   - 定期清理本地分支

### DON'T ❌

1. **不要直接push到main/develop**
   - 总是通过PR合并

2. **不要提交大量未审查的代码**
   - 单个PR不超过500行

3. **不要提交敏感信息**
   - 密码、密钥、Token等
   - 使用.gitignore和环境变量

4. **不要使用force push（除非rebase）**
   - 使用`--force-with-lease`代替`--force`

5. **不要忽略CI失败**
   - 修复所有测试失败
   - 修复所有lint警告

---

## 版本历史

- **v1.0** (2024-11-17): 初始版本，定义完整Git工作流

---

Generated with Claude Code
