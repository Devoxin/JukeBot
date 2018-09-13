package jukebot.utils

import okhttp3.Response
import org.json.JSONObject

fun Response.json(): JSONObject? {
    val body = body()

    body().use {
        return if (isSuccessful && body != null) {
            JSONObject(body()!!.string())
        } else {
            null
        }
    }
}

fun String.toTitleCase(): String {
    return this[0].toUpperCase() + this.substring(1)
}
