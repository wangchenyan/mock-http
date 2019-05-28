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
    private var hasInit = false

    companion object {
        fun get(): MockHttp {
            return MockHttp.SingletonHolder.instance
        }
    }

    private object SingletonHolder {
        @SuppressLint("StaticFieldLeak")
        val instance = MockHttp()
    }

    /**
     * 启动 MOCK 服务，开始 MOCK<br>
     * 如果是多进程应用，只需要在主进程中初始化
     *
     * @param ctx 上下文
     * @param options MOCK HTTP 配置项
     */
    fun init(ctx: Context, options: MockHttpOptions) {
        if (context != null) {
            Log.w("MOCK-HTTP", "mock-http has init, destroy first")
            destroy()
        }

        Log.d("MOCK-HTTP", "init")

        context = ctx.applicationContext
        mockHttpOptions = options
        mockPreference = context!!.getSharedPreferences(context!!.packageName + ".com.github.wangchenyan.mock-http", Context.MODE_PRIVATE)
        httpEntityMap = mutableMapOf()
        mockHttpServer = MockHttpServer()
        mockHttpServer?.startServer(context!!)

        hasInit = true
    }

    /**
     * 是否已经初始化
     */
    fun hasInit(): Boolean {
        return hasInit
    }

    /**
     * 停止 MOCK 服务，释放资源
     */
    fun destroy() {
        Log.d("MOCK-HTTP", "destroy")

        context = null
        mockHttpOptions = null
        mockPreference = null
        httpEntityMap?.clear()
        httpEntityMap = null
        mockHttpServer?.stopServer()
        mockHttpServer = null

        hasInit = false
    }

    /**
     * 获取 MOCK HTTP 配置项
     */
    fun getMockHttpOptions(): MockHttpOptions {
        checkInit()

        return mockHttpOptions!!
    }

    /**
     * 获取 MOCK 服务器地址
     */
    fun getMockAddress(): String {
        checkInit()

        val wifiManager = context!!.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val ipAddress = wifiManager.connectionInfo.ipAddress
        val ip = (ipAddress and 0xFF).toString() + "." +
                (ipAddress shr 8 and 0xFF) + "." +
                (ipAddress shr 16 and 0xFF) + "." +
                (ipAddress shr 24 and 0xFF)
        return "$ip:${mockHttpOptions!!.getMockServerPort()}"
    }

    /**
     * 获取 MOCK 响应结果
     *
     * @param path 路径
     */
    fun getMockResponseBody(path: String): String? {
        checkInit()

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
    fun request(path: String, entity: MockHttpEntity) {
        checkInit()

        httpEntityMap!![path] = entity
    }

    /**
     * 获取 HTTP 请求信息集合
     *
     * @param mock true: 已 MOCK 请求, false: 未 MOCK 请求
     */
    fun getRequestList(mock: Boolean): List<String> {
        checkInit()

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
    fun getRequest(path: String): MockHttpEntity? {
        checkInit()

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
        checkInit()

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
        checkInit()

        mockPreference!!.edit().remove(path).apply()
    }

    /**
     * 检查是否初始化
     */
    private fun checkInit() {
        if (context == null) {
            throw IllegalStateException("mock-http not init, please init first.")
        }
    }
}