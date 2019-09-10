package me.wcy.mockhttp

import android.util.Log

/**
 * Created by wcy on 2019/5/26.
 */
class MockHttpOptions private constructor() {
    private var mockServerPort = 3001
    private var mockSleepTime = 0L
    private var logEnable = false
    private var logTag = "MOCK-HTTP-LOGGER"
    private var logLevel = Log.DEBUG

    /**
     * MOCK 服务端口
     */
    fun getMockServerPort(): Int {
        return mockServerPort
    }

    /**
     * 获取 MOCK HTTP 等待时间
     */
    fun getMockSleepTime(): Long {
        return mockSleepTime
    }

    /**
     * 是否打印日志
     */
    fun isLogEnable(): Boolean {
        return logEnable
    }

    /**
     * 日志标签
     */
    fun getLogTag(): String {
        return logTag
    }

    /**
     * 日志级别
     */
    fun getLogLevel(): Int {
        return logLevel
    }

    class Builder {
        private val options = MockHttpOptions()

        /**
         * 设置 MOCK 服务端口
         *
         * @param port 端口
         */
        fun setMockServerPort(port: Int): Builder {
            options.mockServerPort = port
            return this
        }

        /**
         * 设置 MOCK HTTP 等待时间
         *
         * @param milli 毫秒，默认为 0
         */
        fun setMockSleepTime(milli: Long): Builder {
            options.mockSleepTime = milli
            return this
        }

        /**
         * 设置是否打印日志，开启 Mock 后才能打印日志
         *
         * @param enable 是否打印日志
         */
        fun setLogEnable(enable: Boolean): Builder {
            options.logEnable = enable
            return this
        }

        /**
         * 设置日志标签
         *
         * @param tag 日志标签
         */
        fun setLogTag(tag: String): Builder {
            options.logTag = tag
            return this
        }

        /**
         * 设置日志级别，参考 [Log]
         *
         * @param level 日志级别，支持 [Log.VERBOSE] [Log.DEBUG] [Log.INFO] [Log.WARN] [Log.ERROR]
         */
        fun setLogLevel(level: Int): Builder {
            options.logLevel = level
            return this
        }

        fun build(): MockHttpOptions {
            return options
        }
    }
}