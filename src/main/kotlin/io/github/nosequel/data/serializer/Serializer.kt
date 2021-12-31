package io.github.nosequel.data.serializer

abstract class Serializer<T>
{
    abstract val type: Class<T>

    abstract fun serialize(
        value: T
    ): String?

    abstract fun deserialize(
        value: String
    ): T?
}