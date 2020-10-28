package me.wcy.mockhttp

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Created by wcy on 2019/5/23.
 */
class MockHttpInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(chain.request())
    }
}