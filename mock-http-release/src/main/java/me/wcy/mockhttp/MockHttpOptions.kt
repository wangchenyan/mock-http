package me.wcy.mockhttp

import android.util.Log

/**
 * MockHttp 配置项
 * Created by wcy on 2019/5/26.
 */
class MockHttpOptions private constructor() {

    /**
     * MOCK 服务端口，如果已经启动服务，则端口设置不会生效
     */
    fun getMockServerPort(): Int {
        return 0
    }

    /**
     * 获取 MOCK HTTP 等待时间
     */
    fun getMockSleepTime(): Long {
        return 0
    }

    /**
     * 是否打印日志
     */
    fun isLogEnable(): Boolean {
        return false
    }

    /**
     * 日志标签
     */
    fun getLogTag(): String {
        return ""
    }

    /**
     * 日志级别
     */
    fun getLogLevel(): Int {
        return 0
    }

    class Builder {
        private val options = MockHttpOptions()

        /**
         * 设置 MOCK 服务端口
         *
         * @param port 端口
         */
        fun setMockServerPort(port: Int): Builder {
            return this
        }

        /**
         * 设置 MOCK HTTP 等待时间
         *
         * @param milli 毫秒，默认为 0
         */
        fun setMockSleepTime(milli: Long): Builder {
            return this
        }

        /**
         * 设置是否打印日志，开启 Mock 后才能打印日志
         *
         * @param enable 是否打印日志
         */
        fun setLogEnable(enable: Boolean): Builder {
            return this
        }

        /**
         * 设置日志标签
         *
         * @param tag 日志标签
         */
        fun setLogTag(tag: String): Builder {
            return this
        }

        /**
         * 设置日志级别，参考 [Log]
         *
         * @param level 日志级别，支持 [Log.VERBOSE] [Log.DEBUG] [Log.INFO] [Log.WARN] [Log.ERROR]
         */
        fun setLogLevel(level: Int): Builder {
            return this
        }

        fun build(): MockHttpOptions {
            return options
        }
    }
}