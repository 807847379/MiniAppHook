# MiniAppHook - 微信小程序自动化测试 LSPosed 模块

## 功能

- ✅ Hook 微信 WebView 注入 JS 代码
- ✅ 通过 HTTP API 控制小程序操作
- ✅ 根据按钮文字点击
- ✅ 获取页面元素信息
- ✅ 输入文本到表单

## 项目结构

```
MiniAppHook/
├── app/
│   └── src/main/
│       ├── java/com/example/miniapphook/
│       │   ├── MainHook.java      # LSPosed 主模块
│       │   └── HttpServer.java    # HTTP 控制服务器
│       ├── assets/
│       │   ├── inject.js         # 注入到 WebView 的 JS
│       │   └── module.prop        # 模块配置
│       └── AndroidManifest.xml
├── test_client.py                 # Python 测试客户端
└── README.md
```

## API 接口

| 方法 | 路径 | 参数 | 说明 |
|------|------|------|------|
| GET | /status | - | 获取模块状态 |
| POST | /click | {"text": "发送"} | 点击指定文字的按钮 |
| POST | /input | {"selector": "#input", "text": "hello"} | 输入文本 |
| GET | /page | - | 获取页面信息 |

## 使用方法

### 1. 编译模块

使用 Android Studio 打开项目，编译 Debug APK。

### 2. 安装模块

1. 将 APK 推送到模拟器: `adb install MiniAppHook.apk`
2. 打开 LSPosed Manager
3. 找到 MiniAppHook 模块
4. 勾选 `com.tencent.mm` (微信)
5. 重启模拟器

### 3. 启动测试

```bash
# 查看状态
python test_client.py status

# 点击按钮
python test_client.py click 发送

# 获取页面信息
python test_client.py page

# 输入文本
python test_client.py input "#username" "test@example.com"
```

## JS 方法

注入后可在小程序控制台使用:

```javascript
// 点击按钮
__MiniAppHook__.clickByText("发送")

// 获取页面信息
__MiniAppHook__.getPageInfo()

// 获取可点击元素
__MiniAppHook__.getClickableElements()

// 输入文本
__MiniAppHook__.inputText("#input", "hello")

// 滑动页面
__MiniAppHook__.swipe("up", 300)
```

## 注意事项

1. 确保模拟器已获取 Root 权限
2. 确保 LSPosed 和 Zygisk 已正确配置
3. HTTP 服务器端口为 8888
4. 确保 PC 和模拟器在同一网络
