package io.github.nosequel.data.connection.redis

import io.github.nosequel.data.connection.ConnectionPool
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import kotlin.concurrent.thread

abstract class RedisConnectionPool : ConnectionPool<Jedis>()
{
    var pool: JedisPool? = null
        get()
        {
            if (field == null)
            {
                field = createPool()
            }

            return field
        }

    abstract var hostname: String?
    abstract var port: Int?

    private fun createPool(): JedisPool
    {
        return JedisPool(hostname, port!!)
    }

    fun execute(action: (Jedis) -> Unit)
    {
        thread(true) {
            val pool = this.getConnectionPool()

            action.invoke(pool)
            pool.close()
        }
    }
}

class NoAuthRedisConnectionPool: RedisConnectionPool()
{
    override var hostname: String? = "127.0.0.1"
    override var port: Int? = 27017

    override fun getConnectionPool(): Jedis
    {
        return this.pool!!.resource
    }
}

class PasswordRedisConnectionPool: RedisConnectionPool()
{
    override var hostname: String? = "127.0.0.1"
    override var port: Int? = 27017

    var password: String? = ""

    override fun getConnectionPool(): Jedis {
        return this.pool!!.resource.apply {
            this.auth(password)
        }
    }
}

class PasswordUserRedisConnectionPool: RedisConnectionPool()
{
    override var hostname: String? = "127.0.0.1"
    override var port: Int? = 27017

    var password: String? = ""
    var username: String? = ""

    override fun getConnectionPool(): Jedis
    {
        return this.pool!!.resource.apply {
            this.auth(username, password)
        }
    }
}