package me.wcy.mockhttp

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.koushikdutta.async.http.server.AsyncHttpServer
import java.io.IOException
import java.io.InputStream
import java.io.StringBufferInputStream

/**
 * Created by wcy on 2020-01-16.
 */
internal object MockHttpUtils {
    private val uiHandler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }

    fun initThirdParty(context: Context, asyncHttpServer: AsyncHttpServer) {
        asyncHttpServer.get("/lib/codemirror.css") { request, response ->
            val stream = getAssetsStream(context, "/lib/codemirror.css")
            response.setContentType("text/css")
            response.sendStream(stream, stream.available().toLong())
        }

        asyncHttpServer.get("/addon/lint/lint.css") { request, response ->
            val stream = getAssetsStream(context, "/addon/lint/lint.css")
            response.setContentType("text/css")
            response.sendStream(stream, stream.available().toLong())
        }

        asyncHttpServer.get("/lib/codemirror.js") { request, response ->
            val stream = getAssetsStream(context, "/lib/codemirror.js")
            response.setContentType("application/javascript")
            response.sendStream(stream, stream.available().toLong())
        }

        asyncHttpServer.get("/lib/jsonlint.js") { request, response ->
            val stream = getAssetsStream(context, "/lib/jsonlint.js")
            response.setContentType("application/javascript")
            response.sendStream(stream, stream.available().toLong())
        }

        asyncHttpServer.get("/mode/javascript/javascript.js") { request, response ->
            val stream = getAssetsStream(context, "/mode/javascript/javascript.js")
            response.setContentType("application/javascript")
            response.sendStream(stream, stream.available().toLong())
        }

        asyncHttpServer.get("/addon/lint/lint.js") { request, response ->
            val stream = getAssetsStream(context, "/addon/lint/lint.js")
            response.setContentType("application/javascript")
            response.sendStream(stream, stream.available().toLong())
        }

        asyncHttpServer.get("/addon/lint/json-lint.js") { request, response ->
            val stream = getAssetsStream(context, "/addon/lint/json-lint.js")
            response.setContentType("application/javascript")
            response.sendStream(stream, stream.available().toLong())
        }
    }

    fun getAssetsStream(context: Context, name: String): InputStream {
        try {
            return context.assets.open("mock-http$name")
        } catch (e: IOException) {
            e.printStackTrace()
            return StringBufferInputStream(e.message)
        }
    }

    fun runOnUi(r: Runnable) {
        uiHandler.post(r)
    }
}