package me.wcy.mockhttp

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.net.wifi.WifiManager
import android.util.Log

/**
 * MOCK HTTP
 * Created by wcy on 2019/5/23.
 */
class MockHttp private constructor() {
    private var context: Context? = null
    private var mockHttpServer: MockHttpServer? = null
    private var mockHttpOptions: MockHttpOptions? = null
    private var mockPreference: SharedPreferences? = null
    private var httpEntityMap: MutableMap<String, MockHttpEntity>? = null
    private var hasStart = false

    companion object {
        fun get(): MockHttp {
            return SingletonHolder.instance
        }
    }

    private object SingletonHolder {
        @SuppressLint("StaticFieldLeak")
        val instance = MockHttp()
    }

    /**
     * 获取 MOCK HTTP 配置项
     */
    fun getMockHttpOptions(): MockHttpOptions? {
        return mockHttpOptions
    }

    /**
     * 设置 MOCK HTTP 配置项<br>
     * 需要在 [start] 之前调用，否则将使用默认配置
     *
     * @param options MOCK HTTP 配置项
     * @see MockHttpOptions
     */
    fun setMockHttpOptions(options: MockHttpOptions) {
        this.mockHttpOptions = options
    }

    /**
     * 启动 MOCK 服务，开始 MOCK<br>
     * 如果是多进程应用，只需要在主进程中初始化
     *
     * @param ctx 上下文
     */
    fun start(ctx: Context) {
        if (hasStart()) {
            Log.w("MOCK-HTTP", "mock-http has start, stop it first")
            stop()
        }

        Log.d("MOCK-HTTP", "start")

        if (mockHttpOptions == null) {
            mockHttpOptions = MockHttpOptions.Builder().build()
        }

        context = ctx.applicationContext
        mockPreference = context!!.getSharedPreferences(context!!.packageName + ".com.github.wangchenyan.mock-http", Context.MODE_PRIVATE)
        httpEntityMap = mutableMapOf()
        mockHttpServer = MockHttpServer()
        mockHttpServer?.startServer(context!!)

        hasStart = true
    }

    /**
     * 停止 MOCK 服务，释放资源
     */
    fun stop() {
        Log.d("MOCK-HTTP", "stop")

        context = null
        mockPreference = null
        httpEntityMap?.clear()
        httpEntityMap = null
        mockHttpServer?.stopServer()
        mockHttpServer = null

        hasStart = false
    }

    /**
     * 是否已经启动
     */
    fun hasStart(): Boolean {
        return hasStart
    }

    /**
     * 获取 MOCK 服务器地址
     */
    fun getMockAddress(): String {
        val port = mockHttpOptions?.getMockServerPort() ?: 0

        val wifiManager = context!!.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val ipAddress = wifiManager.connectionInfo.ipAddress
        val ip = (ipAddress and 0xFF).toString() + "." +
                (ipAddress shr 8 and 0xFF) + "." +
                (ipAddress shr 16 and 0xFF) + "." +
                (ipAddress shr 24 and 0xFF)
        return "$ip:${port}"
    }

    /**
     * 获取 MOCK 响应结果
     *
     * @param path 路径
     */
    internal fun getMockResponseBody(path: String): String? {
        checkStart()

        if (mockPreference!!.contains(path)) {
            val json = mockPreference!!.getString(path, null)
            val entity = MockHttpEntity.fromJson(json)
            return entity?.responseBody
        }

        return null
    }

    /**
     * 记录 HTTP 请求
     *
     * @param path 路径
     * @param entity 请求实体
     */
    internal fun request(path: String, entity: MockHttpEntity) {
        checkStart()

        httpEntityMap!![path] = entity
    }

    /**
     * 获取 HTTP 请求信息集合
     *
     * @param mock true: 已 MOCK 请求, false: 未 MOCK 请求
     */
    internal fun getRequestList(mock: Boolean): List<String> {
        checkStart()

        val mockList = mockPreference!!.all.keys
        if (mock) {
            return mockList.toList()
        }

        val unmockList = mutableListOf<String>()
        for (path in httpEntityMap!!.keys) {
            if (!mockList.contains(path)) {
                unmockList.add(path)
            }
        }

        return unmockList
    }

    /**
     * 获取 HTTP 请求信息
     *
     * @param path 路径
     */
    internal fun getRequest(path: String): MockHttpEntity? {
        checkStart()

        if (mockPreference!!.contains(path)) {
            return MockHttpEntity.fromJson(mockPreference!!.getString(path, null))
        } else {
            return httpEntityMap!![path]
        }
    }

    /**
     * MOCK HTTP 请求
     *
     * @param path 路径
     * @param responseBody 响应体
     */
    internal fun mock(path: String, responseBody: String) {
        checkStart()

        var entity: MockHttpEntity?
        if (httpEntityMap!!.containsKey(path)) {
            entity = httpEntityMap!![path]
        } else if (mockPreference!!.contains(path)) {
            entity = MockHttpEntity.fromJson(mockPreference!!.getString(path, null))
        } else {
            entity = MockHttpEntity(path)
        }

        if (entity == null) {
            entity = MockHttpEntity(path)
        }

        entity.responseBody = responseBody
        mockPreference!!.edit().putString(path, entity.toJson()).apply()
    }

    /**
     * 取消 MOCK HTTP 请求
     *
     * @param path 路径
     */
    internal fun unmock(path: String) {
        checkStart()

        mockPreference!!.edit().remove(path).apply()
    }

    /**
     * 检查是否初始化
     */
    private fun checkStart() {
        if (!hasStart()) {
            throw IllegalStateException("mock-http not start, please start first.")
        }
    }
}