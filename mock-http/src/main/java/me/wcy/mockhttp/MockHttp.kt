package me.wcy.mockhttp

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.net.wifi.WifiManager

/**
 * Created by wcy on 2019/5/23.
 */
class MockHttp private constructor() {
    private var context: Context? = null
    private var mockHttpServer: MockHttpServer? = null
    private var mockHttpOptions: MockHttpOptions? = null
    private var mockPreference: SharedPreferences? = null
    private val httpEntityMap = mutableMapOf<String, MockHttpEntity>()

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
     * 初始化<br>
     * 如果是多进程应用，只需要在主进程中初始化
     *
     * @param context 上下文
     * @param options MOCK HTTP 配置项
     */
    fun init(context: Context, options: MockHttpOptions): MockHttp {
        this.context = context.applicationContext
        this.mockHttpOptions = options
        this.mockPreference = context.getSharedPreferences(context.packageName + ".me.wcy.mock-http", Context.MODE_PRIVATE)

        if (this.mockHttpOptions!!.isMockEnabled()) {
            mockHttpServer = MockHttpServer(this.context!!)
            mockHttpServer?.startServer()
        }

        return this
    }

    /**
     * MOCK HTTP 配置项
     */
    fun getMockHttpOptions(): MockHttpOptions {
        checkInit()

        return mockHttpOptions!!
    }

    /**
     * MOCK 服务器地址
     */
    fun getMockAddress(): String {
        checkInit()

        if (!mockHttpOptions!!.isMockEnabled()) {
            return ""
        }

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
     */
    fun getMockResponseBody(path: String): String? {
        if (!mockHttpOptions!!.isMockEnabled()) {
            return null
        }

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
     */
    fun request(path: String, entity: MockHttpEntity) {
        httpEntityMap[path] = entity
    }

    /**
     * 获取 HTTP 请求信息集合
     */
    fun getRequestList(mock: Boolean): List<String> {
        checkInit()

        val mockList = mockPreference!!.all.keys
        if (mock) {
            return mockList.toList()
        }

        val unmockList = mutableListOf<String>()
        for (path in httpEntityMap.keys) {
            if (!mockList.contains(path)) {
                unmockList.add(path)
            }
        }

        return unmockList
    }

    /**
     * 获取 HTTP 请求信息
     */
    fun getRequest(path: String): MockHttpEntity? {
        if (mockPreference!!.contains(path)) {
            return MockHttpEntity.fromJson(mockPreference!!.getString(path, null))
        } else {
            return httpEntityMap[path]
        }
    }

    /**
     * MOCK HTTP 请求
     */
    internal fun mock(path: String, responseBody: String) {
        checkInit()

        var entity: MockHttpEntity?
        if (httpEntityMap.containsKey(path)) {
            entity = httpEntityMap[path]
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
     */
    internal fun unmock(path: String) {
        checkInit()

        mockPreference!!.edit().remove(path).apply()
    }

    private fun checkInit() {
        if (context == null) {
            throw IllegalStateException("mock-http not init, please check.")
        }
    }
}