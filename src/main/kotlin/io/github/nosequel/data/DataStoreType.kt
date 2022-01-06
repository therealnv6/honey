package io.github.nosequel.data

import io.github.nosequel.data.connection.ConnectionPool
import io.github.nosequel.data.connection.mongo.MongoConnectionPool
import io.github.nosequel.data.connection.redis.RedisConnectionPool
import io.github.nosequel.data.store.StoreType
import io.github.nosequel.data.store.type.MongoStoreType
import io.github.nosequel.data.store.type.RedisStoreType

enum class DataStoreType(
    val type: Class<out ConnectionPool<*>>,
    val dataType: Class<out StoreType<*, *>>
)
{
    MONGO(
        MongoConnectionPool::class.java,
        MongoStoreType::class.java
    ),

    REDIS(
        RedisConnectionPool::class.java,
        RedisStoreType::class.java
    )
}