package io.github.nosequel.data.serializer.type

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy
import io.github.nosequel.data.serializer.Serializer

object GsonSerializer
{
    val GSON: Gson = GsonBuilder()
        .setLongSerializationPolicy(LongSerializationPolicy.STRING)
        .serializeNulls().create()

    inline fun <reified T> createSerializer(): Serializer<T>
    {
        return GsonSerialization(T::class.java)
    }
}

class GsonSerialization<T>(override val type: Class<T>) : Serializer<T>()
{
    override fun serialize(value: T): String?
    {
        return GsonSerializer.GSON.toJson(value)
    }

    override fun deserialize(value: String): T?
    {
        return GsonSerializer.GSON.fromJson(value, type)
    }
}