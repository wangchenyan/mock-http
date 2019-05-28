package me.wcy.mockhttp

/**
 * Created by wcy on 2019/5/26.
 */
class MockHttpOptions private constructor() {
    private var mockServerPort = 3001
    private var mockSleepTime = 0L

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

    class Builder {
        private val mockHttpOptions = MockHttpOptions()

        /**
         * 设置 MOCK 服务端口
         *
         * @param port 端口
         */
        fun setMockServerPort(port: Int): Builder {
            mockHttpOptions.mockServerPort = port
            return this
        }

        /**
         * 设置 MOCK HTTP 等待时间
         *
         * @param milli 毫秒，默认为 0
         */
        fun setMockSleepTime(milli: Long): Builder {
            mockHttpOptions.mockSleepTime = milli
            return this
        }

        fun build(): MockHttpOptions {
            return mockHttpOptions
        }
    }
}