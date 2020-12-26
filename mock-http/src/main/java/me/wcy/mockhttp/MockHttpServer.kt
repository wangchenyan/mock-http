package me.wcy.mockhttp

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.koushikdutta.async.AsyncServer
import com.koushikdutta.async.callback.CompletedCallback
import com.koushikdutta.async.http.server.AsyncHttpServer
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLDecoder

/**
 * Created by wcy on 2019/5/24.
 */
internal class MockHttpServer {
    private var asyncHttpServer: AsyncHttpServer? = null
    private var asyncServer: AsyncServer? = null

    companion object {
        private const val TAG = "MockHttpServer"
    }

    fun startServer(context: Context) {
        asyncHttpServer = AsyncHttpServer()
        asyncServer = AsyncServer()

        MockHttpUtils.initThirdParty(context, asyncHttpServer!!)

        asyncHttpServer!!.get("/") { request, response ->
            val stream = MockHttpUtils.getAssetsStream(context, "/index.html")
            response.setContentType("text/html")
            response.sendStream(stream, stream.available().toLong())
        }

        asyncHttpServer!!.get("/request") { request, response ->
            val stream = MockHttpUtils.getAssetsStream(context, "/request.html")
            response.setContentType("text/html")
            response.sendStream(stream, stream.available().toLong())
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
                response.code(500).send(e.message)
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
                response.code(500).send(e.message)
            }
        }

        asyncHttpServer!!.post("/mock") { request, response ->
            try {
                val requestBody = request.body.get() as JSONObject
                val path = requestBody.getString("path")
                val responseBody = URLDecoder.decode(requestBody.getString("responseBody"), Charsets.UTF_8.name())
                MockHttp.get().mock(path, responseBody)
                response.send("success")
            } catch (e: Exception) {
                e.printStackTrace()
                response.code(500).send(e.message)
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
                response.code(500).send(e.message)
            }
        }

        val options = MockHttp.get().getMockHttpOptions()!!
        asyncHttpServer!!.errorCallback = CompletedCallback {
            Log.e(TAG, "mock http server error", it)
            MockHttpUtils.runOnUi(Runnable {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            })
        }
        asyncHttpServer!!.listen(asyncServer, options.getMockServerPort())
    }

    fun stopServer() {
        asyncHttpServer?.stop()
        asyncServer?.stop()
        asyncHttpServer = null
        asyncServer = null
    }
}