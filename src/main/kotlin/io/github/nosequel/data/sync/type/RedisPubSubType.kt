package io.github.nosequel.data.sync.type

import io.github.nosequel.data.connection.redis.RedisConnectionPool
import io.github.nosequel.data.serializer.Serializer
import io.github.nosequel.data.sync.PubSubType
import redis.clients.jedis.JedisPubSub

class RedisPubSubType<T>(
    private val connectionPool: RedisConnectionPool,
    private val serializer: Serializer<T>
) : PubSubType<T>()
{
    override fun load()
    {
        connectionPool.execute {
            it.subscribe(object : JedisPubSub()
            {
                override fun onMessage(
                    channel: String,
                    message: String
                )
                {
                    this@RedisPubSubType.incoming(message)
                }
            }, this.id)
        }
    }

    override fun incoming(message: String)
    {
        val data = this.serializer.deserialize(message)

        if (data != null)
        {
            this.handle(data)
        }
    }

    override fun publish(value: T)
    {
        connectionPool.execute {
            val data = this.serializer.serialize(value)

            if (data != null)
            {
                it.publish(this.id, data)
            }
        }
    }
}