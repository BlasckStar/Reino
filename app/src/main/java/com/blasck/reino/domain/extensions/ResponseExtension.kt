package com.blasck.reino.domain.extensions

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import java.io.IOException
import kotlin.jvm.Throws

object ResponseExtension {
    @Throws(IOException::class)
    inline fun <reified A> ResponseBody.toObject(): A{
        return string().fromJson()
    }

    inline fun <reified A> String?.fromJson(): A {
        return GsonBuilder().create().fromJson(this, object: TypeToken<A>(){}.type)
    }
}