package me.wcy.mockhttp

import android.annotation.SuppressLint
import android.content.Context

/**
 * MOCK HTTP
 * Created by wcy on 2019/5/23.
 */
class MockHttp private constructor() {

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
        return null
    }

    /**
     * 设置 MOCK HTTP 配置项<br>
     * 需要在 [start] 之前调用，否则将使用默认配置
     *
     * @param options MOCK HTTP 配置项
     * @see MockHttpOptions
     */
    fun setMockHttpOptions(options: MockHttpOptions) {
    }

    /**
     * 启动 MOCK 服务，开始 MOCK<br>
     * 如果是多进程应用，只需要在主进程中初始化
     *
     * @param ctx 上下文
     */
    fun start(ctx: Context) {
    }

    /**
     * 停止 MOCK 服务，释放资源
     */
    fun stop() {
    }

    /**
     * 是否已经启动
     */
    fun hasStart(): Boolean {
        return false
    }

    /**
     * 获取 MOCK 服务器地址
     */
    fun getMockAddress(): String {
        return ""
    }
}