# Android MOCK HTTP 接口新方式

[![](https://jitpack.io/v/wangchenyan/mock-http.svg)](https://jitpack.io/#wangchenyan/mock-http)
![size](https://img.shields.io/badge/size-39k-FF4500.svg?style=flat)
![GitHub](https://img.shields.io/github/license/wangchenyan/mock-http.svg)

MOCK-HTTP 是一个方便、易用的查看和模拟 HTTP 请求的工具，可以代替 Charles，支持打印网络日志。

混淆模式下，包大小增加量为39k。

目前仅支持查看和模拟 OKHTTP 发送的请求。

视频演示：

[![](https://raw.githubusercontent.com/wangchenyan/mock-http/master/art/demo.jpg)](https://v.youku.com/v_show/id_XNDUzNzE5MTM4NA==.html)

架构设计：

![](https://raw.githubusercontent.com/wangchenyan/mock-http/master/art/architecture.jpg)

原理解析：https://juejin.im/post/5cecce125188253a275a3d9f

## 更新记录

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
    implementation 'com.github.wangchenyan:mock-http:1.4'
}
```

**Step 3.** 启用 MOCK-HTTP

```
// 初始化
MockHttp.get().init(applicationContext,
        MockHttpOptions.Builder()
                .setMockServerPort(3001)
                .setMockSleepTime(500)
                .setLogEnable(true)
                .setLogTag("TAG-NAME")
                .setLogLevel(Log.ERROR)
                .build())

// 添加 OKHTTP 拦截器
val okHttpClient = OkHttpClient()
        .newBuilder()
        .addInterceptor(MockHttpInterceptor())
        .build()
```

## 方法

| 方法 | 描述 | 备注 |
| ---- | ---- | ---- |
| init(Context, MockHttpOptions) | 启动 MOCK 服务，开始 MOCK | 如果是多进程应用，只需要在主进程中初始化 |
| destroy() | 停止 MOCK 服务，释放资源 | |
| getMockAddress() | 获取 MOCK 服务器地址 | |
| hasInit() | 是否已经初始化 | |
| MockHttpOptions.Builder().setMockServerPort(Int) | 设置 MOCK 端口 | |
| MockHttpOptions.Builder().setMockSleepTime(Long) | 设置 MOCK 接口等待时长 | 单位毫秒，默认为0 |
| MockHttpOptions.Builder().setLogEnable(Boolean) | 设置是否打印日志 | 开启 Mock 后才能打印日志 |
| MockHttpOptions.Builder().setLogTag(String) | 设置日志标签 | |
| MockHttpOptions.Builder().setLogLevel(Int) | 设置日志级别，参考 android.util.Log | 支持 VERBOSE DEBUG INFO WARN ERROR |

## 致谢

- [AndroidAsync](https://github.com/koush/AndroidAsync)

## 关于作者

掘金：https://juejin.im/user/58abd9f1da2f607e924e945a

微博：http://weibo.com/wangchenyan1993

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
