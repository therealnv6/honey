package io.github.nosequel.data

import io.github.nosequel.data.connection.ConnectionPool
import io.github.nosequel.data.serializer.Serializer
import io.github.nosequel.data.serializer.type.createSerializer
import io.github.nosequel.data.store.StoreType

@Suppress("UNCHECKED_CAST", "UNUSED")
object DataHandler
{
    private val connections: HashMap<Class<out ConnectionPool<*>>, ConnectionPool<*>> = hashMapOf()
    val serializers: HashMap<Class<*>, Serializer<*>> = hashMapOf()

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
            this.load()
        } as StoreType<K, V>
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
}