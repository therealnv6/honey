package io.github.nosequel.data.connection.redis

import io.github.nosequel.data.connection.ConnectionPool
import redis.clients.jedis.Jedis

abstract class RedisConnectionPool : ConnectionPool<Jedis>()
{
    abstract var jedis: Jedis?

    abstract fun createPool(): Jedis
}

class NoAuthRedisConnectionPool: RedisConnectionPool()
{
    override var jedis: Jedis? = null

    var hostname: String? = "127.0.0.1"
    var port: Int? = 27017

    override fun createPool(): Jedis
    {
        return Jedis(hostname, port!!)
    }

    override fun getConnectionPool(): Jedis
    {
        return this.jedis!!
    }
}

class PasswordRedisConnectionPool: RedisConnectionPool()
{
    override var jedis: Jedis? = null

    var hostname: String? = "127.0.0.1"
    var port: Int? = 27017

    var password: String? = ""

    override fun createPool(): Jedis
    {
        return Jedis(hostname, port!!)
    }

    override fun getConnectionPool(): Jedis
    {
        return this.jedis!!.apply {
            this.auth(password)
        }
    }
}

class PasswordUserRedisConnectionPool(): RedisConnectionPool()
{
    override var jedis: Jedis? = null

    var hostname: String? = "127.0.0.1"
    var port: Int? = 27017

    var password: String? = ""
    var username: String? = ""

    override fun createPool(): Jedis
    {
        return Jedis(hostname, port!!)
    }

    override fun getConnectionPool(): Jedis
    {
        if (this.jedis == null)
        {
            this.jedis = createPool()
        }

        return this.jedis!!.apply {
            this.auth(username, password)
        }
    }
}