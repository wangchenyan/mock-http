package me.wcy.mockhttp

import org.json.JSONObject

/**
 * Created by wcy on 2019/5/23.
 */
internal data class MockHttpEntity(
        var path: String = "",
        var requestUrl: String = "",
        var requestMethod: String = "",
        var statusCode: Int = 0,
        var requestHeader: String = "",
        var queryParameter: String = "",
        var requestBody: String = "",
        var responseHeader: String = "",
        var responseBody: String = "") {

    companion object {
        fun fromJson(json: String?): MockHttpEntity? {
            if (json == null) {
                return null
            }

            try {
                val jsonObject = JSONObject(json)
                val entity = MockHttpEntity()
                entity.path = jsonObject.optString("path")
                entity.requestUrl = jsonObject.optString("requestUrl")
                entity.requestMethod = jsonObject.optString("requestMethod")
                entity.statusCode = jsonObject.optInt("statusCode")
                entity.requestHeader = jsonObject.optString("requestHeader")
                entity.queryParameter = jsonObject.optString("queryParameter")
                entity.requestBody = jsonObject.optString("requestBody")
                entity.responseHeader = jsonObject.optString("responseHeader")
                entity.responseBody = jsonObject.getString("responseBody")
                return entity
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return null
        }
    }

    fun toJson(): String {
        val map = mutableMapOf<String, Any>()
        map["path"] = path
        map["requestUrl"] = requestUrl
        map["requestMethod"] = requestMethod
        map["statusCode"] = statusCode
        map["requestHeader"] = requestHeader
        map["queryParameter"] = queryParameter
        map["requestBody"] = requestBody
        map["responseHeader"] = responseHeader
        map["responseBody"] = responseBody
        return JSONObject(map).toString(2)
    }
}