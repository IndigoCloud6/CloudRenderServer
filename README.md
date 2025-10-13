# 虚幻引擎像素流送信令服务器 (CloudRenderServer)

🚀 基于 Spring Boot 3 + Netty + Java 17 的虚幻引擎像素流送信令服务器，提供高性能、可扩展的云渲染解决方案。

## 📋 项目简介

本项目是虚幻引擎像素流送技术的 Java 版本信令服务器实现，支持多实例管理、实时通信和智能监控。通过 WebSocket 协议实现客户端与虚幻引擎应用程序之间的通信桥梁，为云游戏、虚拟仿真、数字孪生等场景提供稳定可靠的服务。

## ✨ 主要特性

### 🏗️ 架构设计
- **分层架构**: 采用 DDD 领域驱动设计，代码结构清晰
- **设计模式**: 应用外观模式、观察者模式、工厂模式等优化代码质量
- **微服务就绪**: 支持水平扩展和分布式部署

### 🚀 核心功能
- **多实例管理**: 支持同时管理多个虚幻引擎像素流实例
  - 实例分组管理：按组批量启动、停止、重启实例
  - 实例生命周期管理：启动、停止、重启、清理等完整生命周期控制
  - 实例状态监控：实时查看实例运行状态和进程信息
- **实时通信**: 基于 WebSocket 的低延迟双向通信
- **智能路由**: 自动匹配客户端与最佳像素流实例
- **连接管理**: 完善的客户端连接状态监控和管理
- **进程管理**: 自动化的虚幻引擎进程启动、停止和监控
- **认证管理**: JWT登录认证
- **系统监控**: 实时监控系统资源使用情况
  - CPU 使用率和详细信息
  - 内存使用情况
  - GPU 信息采集（支持 NVIDIA GPU 通过 nvidia-smi）
  - 网络接口和 IP 地址信息

### 🔧 技术特性
- **高性能**: 基于 Netty 异步网络框架，支持高并发
- **可观测性**: 完善的中文日志系统，便于运维监控
- **配置灵活**: 支持多种像素流配置方案和动态调整
- **监控完善**: 实时系统资源监控和告警
  - 使用 OSHI 库进行跨平台系统信息采集
  - 支持 NVIDIA GPU 监控（nvidia-smi）
  - 自动端口管理和可用性检测

## 🛠️ 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 17 | 基础运行环境 |
| Spring Boot | 3.5.5 | 主框架 |
| Netty | 4.2.6.Final | 网络通信框架 |
| MyBatis Plus | 3.5.14 | 数据持久化 |
| SQLite | 3.50.3.0 | 嵌入式数据库 |
| Log4j2 | 3.5.5 | 日志框架 |
| Lombok | Latest | 代码简化 |
| FastJSON2 | 2.0.58 | JSON 处理 |
| HuTool | 5.8.40 | Java 工具库 |
| OSHI | Latest | 系统和硬件信息采集 |

## 📁 项目结构

```
src/main/java/com/xudri/cloudrenderserver/
├── core/                           # 核心业务模块
│   ├── signaling/                 # 信令服务核心
│   │   ├── SignallingChannelHandler.java      # WebSocket消息处理器
│   │   └── SignallingChannelInitializer.java  # 通道初始化器
│   ├── streaming/                 # 像素流处理
│   │   ├── PixelStreamingLauncher.java        # 像素流启动器
│   │   └── PixelStreamingLauncherManager.java # 启动器管理器
│   └── client/                    # 客户端管理
│       ├── ClientManager.java                 # 客户端连接管理
│       ├── MessageHelper.java                 # 消息处理助手
│       ├── PlayerIdPool.java                  # 播放器ID池
│       └── observer/                          # 观察者模式
│           ├── ClientConnectionObserver.java  # 连接观察者接口
│           └── ClientConnectionSubject.java   # 连接主题
├── config/                        # 配置管理
│   ├── CloudRenderRunner.java                # 应用启动配置
│   └── PixelStreamingConfig.java             # 像素流配置
├── infrastructure/                # 基础设施层
│   ├── repository/               # 数据访问层
│   │   ├── InstanceDao.java                  # 实例数据访问
│   │   ├── ProjectDao.java                   # 项目数据访问
│   │   └── SystemConfigDao.java              # 系统配置数据访问
│   ├── network/                  # 网络通信
│   │   └── SignallingServer.java             # 信令服务器
│   └── monitor/                  # 监控组件
│       ├── InstanceCheckJob.java             # 实例检查任务
│       ├── SystemInfoSendJob.java            # 系统信息发送任务
│       ├── ProcessManagerByPowerShell.java   # PowerShell进程管理
│       ├── ProcessKiller.java                # 进程终止器
│       └── SystemInfoUtil.java               # 系统信息工具
├── application/                   # 应用服务层
│   ├── service/                  # 业务服务
│   │   ├── InstanceService.java              # 实例服务接口
│   │   ├── ProjectService.java               # 项目服务接口
│   │   ├── SystemConfigService.java          # 系统配置服务接口
│   │   ├── ClientManagerService.java         # 客户端管理服务接口
│   │   └── impl/                            # 服务实现
│   └── facade/                   # 外观模式
│       └── PixelStreamingFacade.java         # 像素流服务外观
├── interfaces/                    # 接口层
│   ├── rest/                     # REST API
│   │   ├── InstanceController.java           # 实例管理API
│   │   ├── SignallingServerController.java   # 信令服务API
│   │   ├── SystemInfoController.java         # 系统信息API
│   │   ├── ProcessManagerController.java     # 进程管理API
│   │   ├── RenderProjectController.java      # 渲染项目API
│   │   └── WebContentController.java         # Web内容API
│   └── websocket/                # WebSocket接口(预留)
└── common/                        # 通用组件
    ├── exception/                # 异常处理
    │   ├── Result.java                       # 统一响应结果
    │   ├── ApiErrorCode.java                 # API错误码
    │   ├── ApiException.java                 # API异常
    │   └── IErrorCode.java                   # 错误码接口
    ├── util/                     # 工具类
    │   ├── LoggerUtil.java                   # 统一日志工具
    │   └── MultiInstancePixelStreamingExample.java # 多实例示例
    └── constant/                 # 常量定义
        ├── ClientType.java                   # 客户端类型枚举
        ├── MessageType.java                  # 消息类型枚举
        ├── Instance.java                     # 实例实体
        ├── Project.java                      # 项目实体
        ├── SystemConfig.java                 # 系统配置实体
        ├── HostInfo.java                     # 主机信息实体
        └── Gpu.java                          # GPU信息实体
```

## 🚀 快速开始

### 环境要求

- **Java 17+**
- **Maven 3.6+**
- **Windows 10/11** (支持 PowerShell 进程管理)
- **虚幻引擎 4.x/5.x** (支持像素流插件)

### 安装部署

#### 1. 克隆项目

```bash
git clone https://github.com/IndigoCloud6/CloudRenderServer.git
cd CloudRenderServer
```

#### 2. 编译项目

```bash
# 使用 Maven Wrapper
./mvnw clean compile

# 或使用本地 Maven
mvn clean compile
```

#### 3. 运行应用

```bash
# 开发模式运行
./mvnw spring-boot:run

# 或打包后运行
./mvnw clean package
java -jar target/CloudRenderServer-0.0.1-SNAPSHOT.jar
```

#### 4. 访问服务

- **Web 管理界面**: http://localhost:9091
- **API 文档**: http://localhost:9091/api
- **信令服务端口**: 9999 (可配置)

### Docker 部署 (可选)

```bash
# 构建镜像
docker build -t cloud-render-server .

# 运行容器
docker run -d -p 9091:9091 -p 9999:9999 \
  --name cloud-render-server \
  cloud-render-server
```

## 📝 配置说明

### 应用配置 (application.yml)

```yaml
server:
  port: 9091                    # Web服务端口

spring:
  datasource:
    driver-class-name: org.sqlite.JDBC
    url: jdbc:sqlite:./system.db  # SQLite数据库文件

# 自定义配置
pixel-streaming:
  signaling:
    port: 9999                  # 信令服务器端口
    max-connections: 1000       # 最大连接数
  instance:
    check-interval: 30000       # 实例检查间隔(毫秒)
    max-instances: 10           # 最大实例数
```

### 日志配置 (log4j2.xml)

项目使用分类日志记录：

- **业务日志**: `logs/business.log` - 记录关键业务操作
- **系统日志**: `logs/system.log` - 记录系统运行状态
- **错误日志**: `logs/error.log` - 记录错误和异常
- **调试日志**: `logs/debug.log` - 开发调试信息

## 💼 多实例管理示例

### 基本使用

项目提供了完整的多实例管理示例（`MultiInstancePixelStreamingExample.java`），展示如何管理多个像素流实例：

```java
// 创建实例管理器
PixelStreamingLauncherManager manager = new PixelStreamingLauncherManager();

// 配置像素流参数
PixelStreamingConfig config = new PixelStreamingConfig("127.0.0.1", 8890);
config.setRenderOffscreen(true);  // 后台渲染
config.setResX(1920);              // 分辨率宽度
config.setResY(1080);              // 分辨率高度

// 启动实例（支持分组管理）
String executablePath = "path/to/UnrealEngine.exe";
manager.launch("instance-1", "group-high-res", executablePath, config);
manager.launch("instance-2", "group-high-res", executablePath, config);
```

### 实例控制命令

管理器支持以下控制命令：

| 命令 | 说明 | 示例 |
|------|------|------|
| `status` | 查看所有实例状态 | `status` |
| `status <id>` | 查看指定实例状态 | `status instance-1` |
| `stop <id>` | 停止指定实例 | `stop instance-1` |
| `stop-group <groupId>` | 停止指定组的所有实例 | `stop-group group-high-res` |
| `restart <id>` | 重启指定实例 | `restart instance-1` |
| `restart-group <groupId>` | 重启指定组的所有实例 | `restart-group group-high-res` |
| `cleanup` | 清理已停止的实例 | `cleanup` |

### 实例分组管理

支持将实例按组进行批量管理，适用于以下场景：

- **按分辨率分组**: 高分辨率组、标准分辨率组
- **按用途分组**: 开发测试组、生产环境组
- **按地域分组**: 不同地区的实例组

```java
// 批量停止组内所有实例
int count = manager.stopGroup("group-high-res");

// 批量重启组内所有实例
int count = manager.restartGroup("group-high-res");
```

## 🔌 API 接口

### 实例管理 API

#### 创建并启动实例

```http
POST /instance/createAndStart
Content-Type: application/json

{
  "instanceId": "instance_001",
  "projectId": "project_001",
  "config": {
    "maxFps": "60",
    "encoderMaxQP": "40"
  }
}
```

#### 查询实例状态

```http
GET /instance/status/{instanceId}
```

#### 停止实例

```http
POST /instance/stop/{instanceId}
```

### 系统信息 API

#### 获取服务状态

```http
GET /system/status
```

#### 获取系统资源信息

```http
GET /system/info
```

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "hostName": "render-server-01",
    "osName": "Windows 10",
    "ipv4s": ["192.168.1.100"],
    "cpuInfo": "Intel Core i9-9900K (8 cores, 16 threads)",
    "cpuUsage": 45.32,
    "totalMemorySize": 32.0,
    "usedMemorySize": 16.5,
    "freeMemorySize": 15.5,
    "gpuList": [
      {
        "deviceId": 0,
        "uuid": "GPU-xxxxx",
        "gpuUtil": 0.65,
        "memTotal": 11.0,
        "memUsed": 7.2,
        "memFree": 3.8,
        "driver": "536.23",
        "gpuName": "NVIDIA GeForce RTX 3080",
        "serial": "xxxxx",
        "displayMode": "Enabled",
        "displayActive": "Enabled",
        "temperature": 68.0
      }
    ]
  }
}
```

**系统监控特性**:
- 实时 CPU 使用率监控
- 内存使用情况统计
- NVIDIA GPU 信息采集（需要安装 nvidia-smi）
- 网络接口和 IP 地址信息
- 跨平台支持（基于 OSHI 库）

### 信令服务 API

#### 启动信令服务器

```http
POST /signalling/start
```

#### 停止信令服务器

```http
POST /signalling/stop
```

## 🔧 开发指南

### 本地开发环境搭建

1. **安装 Java 17**
   ```bash
   # Windows (使用 Chocolatey)
   choco install openjdk17
   
   # macOS (使用 Homebrew)
   brew install openjdk@17
   
   # Linux (Ubuntu)
   sudo apt install openjdk-17-jdk
   ```

2. **安装 Maven**
   ```bash
   # Windows
   checo install maven
   
   # macOS
   brew install maven
   
   # Linux
   sudo apt install maven
   ```

3. **IDE 配置**
   - 推荐使用 IntelliJ IDEA
   - 导入 Maven 项目
   - 设置 JDK 17
   - 安装 Lombok 插件

### 代码规范

#### 命名规范
- **类名**: 大驼峰命名 (PixelStreamingLauncher)
- **方法名**: 小驼峰命名 (createInstance)
- **常量**: 全大写下划线分隔 (MAX_CONNECTIONS)
- **包名**: 全小写点分隔 (com.xudri.cloudrenderserver)

#### 日志规范
```java
// 使用统一日志工具
LoggerUtil.logBusiness("业务模块", "操作描述");
LoggerUtil.logInstance(instanceId, "操作类型", "详细信息");
LoggerUtil.logConnection("客户端", clientId, "状态", "详情");

// 错误日志
LoggerUtil.logError(log, "操作名称", "错误描述", exception);
```

#### 注释规范
```java
/**
 * 类功能描述
 * 
 * @author 作者名
 * @version 版本号
 * @since 创建日期
 */
public class ExampleClass {
    
    /**
     * 方法功能描述
     * 
     * @param param1 参数1描述
     * @param param2 参数2描述
     * @return 返回值描述
     */
    public String exampleMethod(String param1, int param2) {
        // 实现逻辑
    }
}
```

### 设计模式应用

#### 外观模式 (Facade Pattern)
```java
@Component
public class PixelStreamingFacade {
    // 统一的服务接口，隐藏内部复杂性
    public Result<String> startPixelStreamingService() {
        // 协调多个服务组件
    }
}
```

#### 观察者模式 (Observer Pattern)
```java
// 监听客户端连接状态变化
public interface ClientConnectionObserver {
    void onClientConnected(Channel channel, ClientType type, String id);
    void onClientDisconnected(Channel channel, ClientType type, String id, String reason);
}
```

### 测试指南

#### 单元测试

```bash
# 运行所有测试
./mvnw test

# 运行特定测试类
./mvnw test -Dtest=PixelStreamingLauncherTest

# 生成测试报告
./mvnw test jacoco:report
```

#### 集成测试

```bash
# 启动测试环境
./mvnw spring-boot:run -Dspring.profiles.active=test

# 使用 curl 测试 API
curl -X POST http://localhost:9091/signalling/start
```

## 📊 监控运维

### 日志监控

项目提供完善的中文日志系统：

```bash
# 查看业务日志
tail -f logs/business.log

# 查看错误日志
tail -f logs/error.log

# 查看系统日志
tail -f logs/system.log
```

### 性能监控

```bash
# JVM 参数优化
java -Xms2g -Xmx4g -XX:+UseG1GC \
     -XX:+HeapDumpOnOutOfMemoryError \
     -XX:HeapDumpPath=./heapdump/ \
     -jar CloudRenderServer-0.0.1-SNAPSHOT.jar
```

### 健康检查

```bash
# 应用健康状态
curl http://localhost:9091/actuator/health

# 系统信息
curl http://localhost:9091/system/info
```

## 🔒 安全建议

### 生产环境配置

1. **修改默认端口**
   ```yaml
   server:
     port: 8443  # 使用非标准端口
   ```

2. **启用HTTPS**
   ```yaml
   server:
     ssl:
       enabled: true
       key-store: classpath:keystore.p12
       key-store-password: your-password
   ```

3. **配置防火墙**
   ```bash
   # 只开放必要端口
   ufw allow 8443
   ufw allow 9999
   ```

### 认证授权

```java
// 实现自定义认证逻辑
@Component
public class AuthenticationHandler {
    public boolean validateClient(String token) {
        // 验证客户端token
    }
}
```

## 🛠️ 故障排除

### 常见问题

#### Q: 信令服务器启动失败
**A**: 检查端口是否被占用
```bash
netstat -tulpn | grep 9999
```

#### Q: 实例启动失败
**A**: 检查虚幻引擎可执行文件路径和权限
```bash
# 检查文件存在性
ls -la /path/to/unreal/engine/executable

# 检查执行权限
chmod +x /path/to/unreal/engine/executable
```

#### Q: WebSocket连接断开
**A**: 检查网络配置和防火墙设置
```bash
# 测试网络连通性
telnet localhost 9999
```

#### Q: GPU 信息无法获取
**A**: 确保已安装 NVIDIA 驱动并配置 nvidia-smi
```bash
# Windows: 检查 nvidia-smi 是否可用
nvidia-smi

# 将 NVIDIA 驱动路径添加到系统环境变量
# 通常路径为: C:\Program Files\NVIDIA Corporation\NVSMI

# Linux: 安装 NVIDIA 驱动
sudo apt install nvidia-driver-xxx
nvidia-smi --query-gpu=index,name,driver_version --format=csv
```

#### Q: 多实例启动时端口冲突
**A**: 系统会自动检测可用端口范围
```java
// 使用端口范围自动分配
int freePort = SystemInfoUtil.findFreePortInRange(8000, 9000);

// 创建配置并使用可用端口
PixelStreamingConfig config = new PixelStreamingConfig("127.0.0.1", freePort);
config.setRenderOffscreen(true);
```

### 调试模式

```bash
# 开启调试日志
java -Dlogging.level.com.xudri.cloudrenderserver=DEBUG \
     -jar CloudRenderServer-0.0.1-SNAPSHOT.jar
```

## 🤝 贡献指南

### 提交代码

1. **Fork 项目**
2. **创建特性分支**
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. **提交更改**
   ```bash
   git commit -m "feat: 添加新功能描述"
   ```
4. **推送分支**
   ```bash
   git push origin feature/your-feature-name
   ```
5. **创建 Pull Request**

### 提交规范

使用 [Conventional Commits](https://www.conventionalcommits.org/) 规范：

- `feat:` 新功能
- `fix:` 修复bug
- `docs:` 文档更新
- `style:` 代码格式调整
- `refactor:` 代码重构
- `test:` 测试相关
- `chore:` 构建/工具更新

## 🚧 未来功能规划

### 1. 分布式部署

**目标**: 支持Agent模式，多服务器节点的分布式部署架构

### 2. 其他规划功能

- **WebRTC 优化**: 更低延迟的流媒体传输
- **安全增强**: 端到端加密、访问控制
- **录制回放**: 支持像素流会话录制和回放

**贡献**: 欢迎社区贡献想法和代码！

## 📄 许可证

本项目采用 [MIT License](LICENSE) 许可证。

## 🙏 致谢

感谢以下开源项目的支持：

- [Spring Boot](https://spring.io/projects/spring-boot) - 微服务框架
- [Netty](https://netty.io/) - 异步网络应用框架
- [MyBatis Plus](https://baomidou.com/) - 数据持久化框架
- [HuTool](https://hutool.cn/) - Java工具类库

## 📞 联系方式

- **项目作者**: MaxYun
- **GitHub**: [IndigoCloud6](https://github.com/IndigoCloud6)
- **Email**: [联系邮箱]

---

🌟 **如果这个项目对您有帮助，请给一个 Star 支持一下！**
