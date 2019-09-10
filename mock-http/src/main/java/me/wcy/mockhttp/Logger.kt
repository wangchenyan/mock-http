package me.wcy.mockhttp

import android.text.TextUtils
import android.util.Log
import java.util.*

/**
 * Created by wcy on 2019-09-10.
 */
object Logger {

    fun logRequest(url: String, method: String, headers: String, params: String, body: String) {
        val options = MockHttp.get().getMockHttpOptions()
        if (!options.isLogEnable()) {
            return
        }

        val log = "┌────── Mock Http Request ──────────────────────────────────────────────────────────────" +
                "\n│ URL: $url" +
                "\n│ " +
                "\n│ Method: $method" +
                "\n│ " +
                "\n│ Headers: ${prettyHeaders(headers)}" +
                "\n│ " +
                "\n│ Parameter: $params" +
                "\n│ " +
                "\n│ Body:" +
                "\n│ ${prettyBody(body)}" +
                "\n└───────────────────────────────────────────────────────────────────────────────────────"

        printLog(log)
    }

    fun logResponse(url: String, status: Int, headers: String, body: String) {
        val options = MockHttp.get().getMockHttpOptions()
        if (!options.isLogEnable()) {
            return
        }

        val log = "┌────── Mock Http Response ─────────────────────────────────────────────────────────────" +
                "\n│ URL: $url" +
                "\n│ " +
                "\n│ Status: $status" +
                "\n│ " +
                "\n│ Headers: ${prettyHeaders(headers)}" +
                "\n│ " +
                "\n│ Body:" +
                "\n│ ${prettyBody(body)}" +
                "\n└───────────────────────────────────────────────────────────────────────────────────────"

        printLog(log)
    }

    private fun printLog(log: String) {
        val options = MockHttp.get().getMockHttpOptions()
        val tag = options.getLogTag()
        val level = options.getLogLevel()
        val logs = log.split("\n")
        for (line in logs) {
            when (level) {
                Log.VERBOSE -> Log.v(tag, line)
                Log.DEBUG -> Log.d(tag, line)
                Log.INFO -> Log.i(tag, line)
                Log.WARN -> Log.w(tag, line)
                Log.ERROR -> Log.e(tag, line)
            }
        }
    }

    private fun prettyHeaders(headers: String): String {
        val list = ArrayList(headers.split("\n"))
        val size = list.size
        for (i in size - 1 downTo 0) {
            if (TextUtils.isEmpty(list[i])) {
                list.removeAt(i)
            }
        }

        val sb = StringBuilder()
        for (i in list.indices) {
            val symbol = if (i == 0) {
                "┌"
            } else if (i == list.size - 1) {
                "└"
            } else {
                "├"
            }
            sb.append("\n│ $symbol ").append(list[i])
        }
        return sb.toString()
    }

    private fun prettyBody(body: String): String {
        val list = body.split("\n")
        val sb = StringBuilder()
        for (line in list) {
            sb.append("\n│ $line")
        }
        return sb.toString()
    }
}