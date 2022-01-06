package io.github.nosequel.data.connection.redis

import io.github.nosequel.data.connection.ConnectionPool
import redis.clients.jedis.Jedis

abstract class RedisConnectionPool : ConnectionPool<Jedis>()
{
    abstract val jedis: Jedis
}

class NoAuthRedisConnectionPool(
    hostname: String,
    port: Int
) : RedisConnectionPool()
{
    override val jedis = Jedis(hostname, port)

    override fun getConnectionPool(): Jedis
    {
        return this.jedis
    }
}