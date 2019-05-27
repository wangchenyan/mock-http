# MOCK-HTTP

[![](https://jitpack.io/v/wangchenyan/mock-http.svg)](https://jitpack.io/#wangchenyan/mock-http)

MOCK-HTTP 是一个方便、易用的查看和模拟 HTTP 请求的工具，可以代替 Charles。目前仅支持查看和模拟 OKHTTP 发送的请求。

![](https://raw.githubusercontent.com/wangchenyan/mock-http/master/art/mock-server.png)

## 前言

以往，我们想要模拟接口返回数据，一般是利用 Charles 作为代理中转，配合在线 MOCK 网站 mocky.io，以模拟接口返回。

一般需要以下几个步骤：

1. 下载、安装 Charles
2. 手机连接 Charles 代理
3. APP 请求接口
4. 访问 mock.io 创建模拟数据网址
5. 使用 Charles 将需要模拟的接口映射到该网址

如果是 HTTPS 请求，则手机还需要安装证书，安装证书也存在很多问题（至今我还没有成功过），过程将更加繁琐。

详细可参考该文章：[模拟服务器返回数据](https://yuanjunli.github.io/2016/12/15/%E6%A8%A1%E6%8B%9F%E6%9C%8D%E5%8A%A1%E5%99%A8%E8%BF%94%E5%9B%9E%E6%95%B0%E6%8D%AE/)

## 提升效率

下面，我将介绍一种新的 MOCK 方案，两步即可模拟接口返回，而且更加易用。

1. 添加 MOCK-HTTP 依赖
2. 打开 WEB 浏览器，尽情模拟接口

更加直观的感受，可以查看视频演示

![](https://raw.githubusercontent.com/wangchenyan/mock-http/master/art/demo.jpg)

[点击查看视频](https://v.youku.com/v_show/id_XNDE5ODgxOTE0OA==.html)

目前仅支持 OKHTTP。

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
    implementation 'com.github.wangchenyan:mock-http:1.+'
}
```

**Step 3.** 启用 MOCK-HTTP

```
// 初始化
MockHttp.get().init(applicationContext,
        MockHttpOptions.Builder()
                .setMockEnable(true)
                .setMockServerPort(3001)
                .setMockSleepTime(500)
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
| init(Context, MockHttpOptions) | 初始化 | 如果是多进程应用，只需要在主进程中初始化 |
| destroy() | 停止 MOCK 服务，释放资源 | |
| getMockAddress() | 获取 MOCK 服务器地址 | |
| MockHttpOptions.Builder().setMockEnable(Boolean) | 设置是否开启 MOCK | |
| MockHttpOptions.Builder().setMockServerPort(Int) | 设置 MOCK 端口 | |
| MockHttpOptions.Builder().setMockSleepTime(Long) | 设置 MOCK 接口等待时长 | 单位毫秒，默认为0 |

## 实现思路

总体思路：

![架构图](https://raw.githubusercontent.com/wangchenyan/mock-http/master/art/architecture.png)

1. 利用 [AndroidAsync](https://github.com/koush/AndroidAsync) 启动一个本地 Server，使局域网中的其他设备可以访问到该设备
2. 利用 OKHTTP 拦截器 Interceptor 拦截 HTTP 请求，返回模拟数据

关键代码：

```
/**
 * 启动一个本地 Server，监听请求
 */
fun startServer() {
    asyncHttpServer!!.get("/") { request, response ->
        response.send(getAssetsContent("index.html"))
    }

    asyncHttpServer!!.get("/request") { request, response ->
        response.send(getAssetsContent("request.html"))
    }

    asyncHttpServer!!.post("/getRequestList") { request, response ->
        try {
            val requestBody = request.body.get() as JSONObject
            val mock = requestBody.getInt("mock") == 1
            val requestList = MockHttp.get().getRequestList(mock)
            response.setContentType("application/json")
            response.send(JSONArray(requestList).toString())
        } catch (e: Exception) {
            e.printStackTrace()
            response.code(500).end()
        }
    }

    ...

    asyncHttpServer!!.listen(asyncServer, MockHttp.get().getMockHttpOptions().getMockServerPort())
}
```

```
/**
 * 拦截 HTTP 请求
 */
override fun intercept(chain: Interceptor.Chain): Response {
    val request = chain.request()

    if (!MockHttp.get().getMockHttpOptions().isMockEnabled()) {
        return chain.proceed(request)
    }

    val response: Response
    val mockResponseBody = MockHttp.get().getMockResponseBody(path)
    if (mockResponseBody != null) {
        response = Response.Builder()
                .body(ResponseBody.create(MediaType.parse("application/json"), mockResponseBody))
                .request(chain.request())
                .protocol(Protocol.HTTP_2)
                .message("Mock")
                .code(200)
                .build()
    } else {
        response = chain.proceed(request)
    }

    var newResponseBody: ResponseBody? = null
    if (isNotFileRequest(subtype)) {
        responseBodyString = responseBody.string()
        newResponseBody = ResponseBody.create(contentType, responseBodyString)
    }

    val httpEntity = MockHttpEntity(path)
    httpEntity.requestHeader = requestHeader
    httpEntity.queryParameter = queryParameter.toString()
    httpEntity.requestBody = requestBody
    httpEntity.responseHeader = responseHeader
    httpEntity.responseBody = formatJson(responseBodyString)
    MockHttp.get().request(path, httpEntity)

    return response.newBuilder().body(newResponseBody).build()
}
```

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
