package me.wcy.mockhttp.sample

import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ScrollView
import kotlinx.android.synthetic.main.activity_main.*
import me.wcy.mockhttp.MockHttp
import me.wcy.mockhttp.MockHttpInterceptor
import me.wcy.mockhttp.MockHttpOptions
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private val okHttpClient = OkHttpClient()
            .newBuilder()
            .addInterceptor(MockHttpInterceptor())
            .build()
    private val executor = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MockHttp.get().init(applicationContext,
                MockHttpOptions.Builder()
                        .setMockEnable(true)
                        .setMockServerPort(3001)
                        .setMockSleepTime(500)
                        .build())

        log("\nMock 地址：\n${MockHttp.get().getMockAddress()}")
    }

    override fun onClick(v: View?) {
        when (v) {
            btn_request -> {
                request()
            }
        }
    }

    private fun request() {
        executor.execute {
            val url = "http://tingapi.ting.baidu.com/v1/restserver/ting?method=baidu.ting.billboard.billList&type=2&size=1&offset=0"
            val ua = Build.BRAND + "/" + Build.MODEL + "/" + Build.VERSION.RELEASE
            val request = Request.Builder()
                    .url(url)
                    .header("User-Agent", ua)
                    .build()

            log("\n开始请求：\n$url")

            val response = okHttpClient.newCall(request).execute()
            val responseBody = response.body()?.string() ?: ""

            log("\n请求结果：\n${formatJson(responseBody)}")
        }
    }

    private fun log(log: String) {
        runOnUiThread {
            tv_network_log.append("\n$log")
            scroll_view.post {
                scroll_view.fullScroll(ScrollView.FOCUS_DOWN)
            }
        }
    }

    private fun formatJson(json: String): String {
        var string: String
        try {
            if (json.startsWith("{")) {
                string = JSONObject(json).toString(2)
            } else if (json.startsWith("[")) {
                string = JSONArray(json).toString(2)
            } else {
                string = json
            }
        } catch (e: JSONException) {
            string = json
        } catch (e1: OutOfMemoryError) {
            string = "Output omitted because of Object size."
        }

        return string
    }
}
