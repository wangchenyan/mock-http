package me.wcy.mockhttp

import android.content.Context
import com.koushikdutta.async.AsyncServer
import com.koushikdutta.async.http.server.AsyncHttpServer
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.URLDecoder
import java.nio.charset.Charset

/**
 * Created by wcy on 2019/5/24.
 */
internal class MockHttpServer {
    private var context: Context? = null
    private var asyncHttpServer: AsyncHttpServer? = null
    private var asyncServer: AsyncServer? = null

    fun startServer(ctx: Context) {
        context = ctx.applicationContext
        asyncHttpServer = AsyncHttpServer()
        asyncServer = AsyncServer()

        asyncHttpServer!!.get("/") { request, response ->
            response.send(getAssetsContent("index.html"))
        }

        asyncHttpServer!!.get("/request") { request, response ->
            response.send(getAssetsContent("request.html"))
        }

        asyncHttpServer!!.post("/getRequestList") { request, response ->
            try {
                val requestBody = request.body.get() as JSONObject
                val mock = requestBody.getInt("mock") == 1
                val requestList = MockHttp.get().getRequestList(mock)
                response.setContentType("application/json")
                response.send(JSONArray(requestList).toString())
            } catch (e: Exception) {
                e.printStackTrace()
                response.code(500).end()
            }
        }

        asyncHttpServer!!.post("/getRequest") { request, response ->
            try {
                val requestBody = request.body.get() as JSONObject
                val path = requestBody.getString("path")
                val httpEntity = MockHttp.get().getRequest(path)
                response.setContentType("application/json")
                response.send(httpEntity?.toJson() ?: "{}")
            } catch (e: Exception) {
                e.printStackTrace()
                response.code(500).end()
            }
        }

        asyncHttpServer!!.post("/mock") { request, response ->
            try {
                val requestBody = request.body.get() as JSONObject
                val path = requestBody.getString("path")
                val responseBody = URLDecoder.decode(requestBody.getString("responseBody"), "utf-8")
                MockHttp.get().mock(path, responseBody)
                response.send("success")
            } catch (e: Exception) {
                e.printStackTrace()
                response.code(500).end()
            }
        }

        asyncHttpServer!!.post("/unmock") { request, response ->
            try {
                val requestBody = request.body.get() as JSONObject
                val path = requestBody.getString("path")
                MockHttp.get().unmock(path)
                response.send("success")
            } catch (e: Exception) {
                e.printStackTrace()
                response.code(500).end()
            }
        }

        asyncHttpServer!!.listen(asyncServer, MockHttp.get().getMockHttpOptions().getMockServerPort())
    }

    fun stopServer() {
        context = null
        asyncHttpServer?.stop()
        asyncServer?.stop()
        asyncHttpServer = null
        asyncServer = null
    }

    private fun getAssetsContent(name: String): String {
        var bis: BufferedInputStream? = null
        try {
            bis = BufferedInputStream(context!!.assets.open("mock-http/$name"))
            val baos = ByteArrayOutputStream()
            val tmp = ByteArray(10240)
            var len = bis.read(tmp)
            while (len > 0) {
                baos.write(tmp, 0, len)
                len = bis.read(tmp)
            }
            return String(baos.toByteArray(), Charset.forName("utf-8"))
        } catch (e: IOException) {
            e.printStackTrace()
            return "MOCK失败，请检查'assets/mock-http'文件夹是否存在"
        } finally {
            if (bis != null) {
                try {
                    bis.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}