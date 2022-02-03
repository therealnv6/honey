package io.github.nosequel.data

import io.github.nosequel.data.connection.ConnectionPool
import io.github.nosequel.data.connection.flatfile.FlatfileConnectionPool
import io.github.nosequel.data.connection.mongo.MongoConnectionPool
import io.github.nosequel.data.connection.redis.RedisConnectionPool
import io.github.nosequel.data.store.StoreType
import io.github.nosequel.data.store.type.FlatfileStoreType
import io.github.nosequel.data.store.type.MongoStoreType
import io.github.nosequel.data.store.type.RedisStoreType
import io.github.nosequel.data.sync.PubSubType
import io.github.nosequel.data.sync.type.RedisPubSubType

enum class DataStoreType(
    val type: Class<out ConnectionPool<*>>,
    val dataType: Class<out StoreType<*, *>>,
    val pubSubType: Class<out PubSubType<*>>?
)
{
    MONGO(
        MongoConnectionPool::class.java,
        MongoStoreType::class.java,
        null
    ),

    REDIS(
        RedisConnectionPool::class.java,
        RedisStoreType::class.java,
        RedisPubSubType::class.java
    ),

    FLATFILE(
        FlatfileConnectionPool::class.java,
        FlatfileStoreType::class.java,
        null
    )
}