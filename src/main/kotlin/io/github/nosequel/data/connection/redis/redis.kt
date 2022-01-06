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

class PasswordRedisConnectionPool(
    hostname: String,
    port: Int,

    private val password: String
): RedisConnectionPool()
{
    override val jedis = Jedis(hostname, port)

    override fun getConnectionPool(): Jedis
    {
        return this.jedis.apply {
            this.auth(password)
        }
    }
}

class PasswordUserRedisConnectionPool(
    hostname: String,
    port: Int,

    private val password: String,
    private val username: String
): RedisConnectionPool()
{
    override val jedis = Jedis(hostname, port)

    override fun getConnectionPool(): Jedis
    {
        return this.jedis.apply {
            this.auth(username, password)
        }
    }
}