package io.github.nosequel.data.store.type

import io.github.nosequel.data.connection.redis.RedisConnectionPool
import io.github.nosequel.data.serializer.Serializer
import io.github.nosequel.data.store.StoreType

class RedisStoreType<K, V>(
    private val connectionPool: RedisConnectionPool,
    private val serializer: Serializer<V>
): StoreType<K, V>()
{
    override fun load(): StoreType<K, V>
    {
        return this
    }

    override fun store(
        key: K,
        value: V
    )
    {
        this.connectionPool.getConnectionPool().hset(
            this.id,
            key.toString(),
            this.serializer.serialize(value)
        )
    }

    override fun retrieve(
        key: K,
        action: ((V) -> Unit)?
    ): V?
    {
        val jedis = this.connectionPool.getConnectionPool()
        val retrieved = jedis.hget(this.id, key.toString())

        if (retrieved != null)
        {
            val value = serializer.deserialize(retrieved)

            if (value != null)
            {
                return value.also {
                    action?.invoke(value)
                }
            }
        }

        return null
    }

    override fun retrieveAll(
        action: ((V) -> Unit)?
    ): Collection<V>
    {
        val jedis = this.connectionPool.getConnectionPool()
        val map = jedis.hgetAll(this.id)

        val returnValues = mutableListOf<V>()

        map.forEach {
            this.serializer.deserialize(it.value)
                ?.let { value ->
                    returnValues += value
                }
        }

        return returnValues
    }

    override fun delete(key: K)
    {
        this.connectionPool.getConnectionPool().hdel(key.toString())
    }
}