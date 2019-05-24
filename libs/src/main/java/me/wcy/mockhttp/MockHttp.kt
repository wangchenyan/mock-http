package me.wcy.mockhttp

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import org.json.JSONObject
import android.net.wifi.WifiManager


/**
 * Created by wcy on 2019/5/23.
 */
class MockHttp {
    private var context: Context? = null
    private var mockServer: MockServer? = null
    private var isMockEnabled = false
    private var mockSleepTime = 0L
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

    fun init(context: Context, isMockEnabled: Boolean) {
        this.context = context.applicationContext
        this.isMockEnabled = isMockEnabled
        this.mockPreference = context.getSharedPreferences(context.packageName + ".me.wcy.mock-http", Context.MODE_PRIVATE)

        if (this.isMockEnabled) {
            mockServer = MockServer(this.context!!)
            mockServer?.startServer()
        }
    }

    fun isMockEnabled(): Boolean {
        return isMockEnabled
    }

    fun getMockSleepTime(): Long {
        return mockSleepTime
    }

    fun setMockSleepTime(milli: Long) {
        mockSleepTime = milli
    }

    fun getMockAddress(): String {
        checkInit()

        if (!isMockEnabled) {
            return ""
        }

        val wifiManager = context!!.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val ipAddress = wifiManager.connectionInfo.ipAddress
        val ip = (ipAddress and 0xFF).toString() + "." +
                (ipAddress shr 8 and 0xFF) + "." +
                (ipAddress shr 16 and 0xFF) + "." +
                (ipAddress shr 24 and 0xFF)
        return "$ip:3000"
    }

    fun getMockResponseBody(path: String): String? {
        if (!isMockEnabled) {
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

    fun request(path: String, entity: MockHttpEntity) {
        httpEntityMap[path] = entity
    }

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

    fun getRequest(path: String): MockHttpEntity? {
        if (mockPreference!!.contains(path)) {
            return MockHttpEntity.fromJson(mockPreference!!.getString(path, null))
        } else {
            return httpEntityMap[path]
        }
    }

    fun mock(path: String, responseBody: String) {
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

    fun unmock(path: String) {
        checkInit()

        mockPreference!!.edit().remove(path).apply()
    }

    private fun checkInit() {
        if (context == null) {
            throw IllegalStateException("mock-http not init, please check.")
        }
    }
}