package io.github.nosequel.data

import io.github.nosequel.data.connection.ConnectionPool
import io.github.nosequel.data.serializer.Serializer
import io.github.nosequel.data.serializer.type.createSerializer
import io.github.nosequel.data.store.StoreType
import io.github.nosequel.data.store.type.MongoStoreType
import io.github.nosequel.data.sync.PubSubType
import io.github.nosequel.data.sync.type.RedisPubSubType
import java.lang.RuntimeException

@Suppress("UNCHECKED_CAST", "UNUSED")
object DataHandler
{
    private val connections = hashMapOf<Class<out ConnectionPool<*>>, ConnectionPool<*>>()

    val pubSubTypes = hashMapOf<Class<*>, MutableList<PubSubType<*>>>()

    val linkedIds = hashMapOf<Class<*>, String>()
    val serializers = hashMapOf<Class<*>, Serializer<*>>()

    const val DEFAULT_ID = "data-store-default"

    fun <T : ConnectionPool<*>> findConnection(
        type: Class<T>
    ): T?
    {
        return (this.connections[type] as T?)
    }

    inline fun <reified T> findSerializer(
        type: Class<T>
    ): Serializer<T>
    {
        return (this.serializers[type] as Serializer<T>?)
            ?: createSerializer()
    }

    fun <T : ConnectionPool<*>> withConnectionPool(
        connectionPool: T
    ): DataHandler
    {
        return this.apply {
            var superClass = connectionPool.javaClass

            while (superClass.superclass != ConnectionPool::class.java)
            {
                superClass = superClass.superclass as Class<T>
            }

            this.connections[superClass] = connectionPool
        }
    }

    fun withSerializer(
        serializer: Serializer<*>
    ): DataHandler
    {
        return this.apply {
            this.serializers[serializer.type] = serializer
        }
    }

    inline fun <reified K, reified V> createStoreType(
        type: DataStoreType,
        action: (StoreType<K, V>.() -> Unit) = {}
    ): StoreType<K, V>
    {
        val connectionPool = this.findConnection(type.type)
        val serializer = this.findSerializer(V::class.java)

        val constructor = type.dataType.getConstructor(
            type.type, Serializer::class.java
        )

        return constructor.newInstance(
            connectionPool, serializer
        ).apply {
            action.invoke(this as StoreType<K, V>)

            val linkedId = linkedIds[V::class.java]

            if (this.id == getIdType<V>() && linkedId != null)
            {
                this.id = linkedId
            } else if (this.id == DEFAULT_ID)
            {
                this.id = getIdType<V>()
            }

            this.load()
        } as StoreType<K, V>
    }

    inline fun <reified T> createPubSubType(
        type: DataStoreType,
        channel: String? = null,
        noinline action: (T) -> Unit = {}
    ): PubSubType<T>
    {
        if (type.pubSubType == null)
        {
            throw RuntimeException("pubSubType value of ${type.name} does not exist.")
        }

        val connectionPool = this.findConnection(type.type)
        val serializer = this.findSerializer(T::class.java)

        val constructor = type.pubSubType.getConstructor(
            type.type, Serializer::class.java
        )

        return constructor.newInstance(connectionPool, serializer).apply {
            val linkedId = linkedIds[T::class.java]

            if (channel != null)
            {
                this.id = channel
            } else if (this.id == getIdType<T>() && linkedId != null)
            {
                this.id = linkedId
            } else if (this.id == DEFAULT_ID)
            {
                this.id = getIdType<T>()
            }

            this.load()
            (this as PubSubType<T>).handle = action

            pubSubTypes.putIfAbsent(T::class.java, mutableListOf())
            pubSubTypes[T::class.java]!! += this
        } as PubSubType<T>
    }

    inline fun <reified T> publish(
        data: T,
        channel: String? = null
    ): DataHandler
    {
        return this.apply {
            this.pubSubTypes[T::class.java]
                ?.filter { channel == null || it.id == channel }
                ?.forEach {
                    (it as PubSubType<T>).publish(data)
                }
        }
    }

    inline fun <reified T : ConnectionPool<*>> withConnectionPool(
        action: (T.() -> Unit) = {}
    ): DataHandler
    {
        return this.apply {
            val connectionPool = T::class.java.newInstance().apply {
                action.invoke(this)
            }

            this.withConnectionPool(connectionPool)
        }
    }

    inline fun <reified T : Serializer<*>> withSerializer(): DataHandler
    {
        return this.apply {
            this.withSerializer(T::class.java.newInstance())
        }
    }

    inline fun <reified T> linkTypeToId(id: String): DataHandler
    {
        return this.linkTypeToId(T::class.java, id)
    }

    fun <T> linkTypeToId(type: Class<T>, id: String): DataHandler
    {
        return this.apply {
            this.linkedIds[type] = id
        }
    }

    inline fun <reified T> getIdType() = this.getIdType(T::class.java)

    fun <T> getIdType(type: Class<T>): String
    {
        return type.simpleName.lowercase()
    }
}