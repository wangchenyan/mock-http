# Android MOCK HTTP 接口新方式

[![](https://jitpack.io/v/wangchenyan/mock-http.svg)](https://jitpack.io/#wangchenyan/mock-http)
![GitHub](https://img.shields.io/github/license/wangchenyan/mock-http.svg)

MOCK-HTTP 是一个方便、易用的查看和模拟 HTTP 请求的工具，可以代替 Charles，支持打印网络日志。

release 模式下仅依赖空壳，mock 功能自动失效，对包大小几乎无影响。

目前仅支持查看和模拟 OkHttp 发送的请求。

视频演示：

[![](https://raw.githubusercontent.com/wangchenyan/mock-http/master/art/demo.jpg)](https://v.youku.com/v_show/id_XNDUzNzE5MTM4NA==.html)

架构设计：

![](https://raw.githubusercontent.com/wangchenyan/mock-http/master/art/architecture.jpg)

原理解析：https://juejin.im/post/6844903855151382535

## 更新记录

`v 1.6`
- 增加 release 模式下的空壳依赖

`v 1.5.1`
- 不处理非文本请求

`v 1.5`
- 优化公开方法

`v 1.4`
- MOCK 页面支持 JSON 校验

## 使用方法

### Gradle

**Step 1.** Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

**Step 2.** Add the dependency

```
dependencies {
    debugImplementation 'com.github.wangchenyan.mock-http:mock-http:+'
    releaseImplementation 'com.github.wangchenyan.mock-http:mock-http-release:+'
}
```

**Step 3.** 启用 MOCK-HTTP

```
// 配置项
val options = MockHttpOptions.Builder()
        .setMockServerPort(5000)
        .setMockSleepTime(500)
        .setLogEnable(true)
        .setLogTag("MAIN-TAG")
        .setLogLevel(Log.ERROR)
        .build()
MockHttp.get().setMockHttpOptions(options)
// 启动 MOCK 服务
MockHttp.get().start(applicationContext)
// 停止 MOCK 服务
MockHttp.get().stop()

// 添加 OKHTTP 拦截器
val okHttpClient = OkHttpClient()
        .newBuilder()
        .addInterceptor(MockHttpInterceptor())
        .build()
```

## 方法

| 方法 | 描述 | 备注 |
| ---- | ---- | ---- |
| setMockHttpOptions(MockHttpOptions) | 设置 MOCK HTTP 配置项 | 需要在 start(Context) 方法之前调用，否则将使用默认配置 |
| start(Context) | 启动 MOCK 服务，开始 MOCK | 如果是多进程应用，只需要在主进程中初始化 |
| stop() | 停止 MOCK 服务，释放资源 | |
| getMockAddress() | 获取 MOCK 服务器地址 | |
| hasStart() | 是否已经启动 | |
| MockHttpOptions.Builder().setMockServerPort(Int) | 设置 MOCK 端口 | |
| MockHttpOptions.Builder().setMockSleepTime(Long) | 设置 MOCK 接口等待时长 | 单位毫秒，默认为0 |
| MockHttpOptions.Builder().setLogEnable(Boolean) | 设置是否打印日志 | 开启 Mock 后才能打印日志 |
| MockHttpOptions.Builder().setLogTag(String) | 设置日志标签 | |
| MockHttpOptions.Builder().setLogLevel(Int) | 设置日志级别，参考 android.util.Log | 支持 VERBOSE DEBUG INFO WARN ERROR |

## 致谢

- [AndroidAsync](https://github.com/koush/AndroidAsync)

## 关于作者

掘金：https://juejin.im/user/2313028193754168

微博：https://weibo.com/wangchenyan1993

## License

    Copyright 2019 wangchenyan

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
