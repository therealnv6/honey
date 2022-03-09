package io.github.nosequel.data.store.type

import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import io.github.nosequel.data.connection.mongo.MongoConnectionPool
import io.github.nosequel.data.serializer.Serializer
import io.github.nosequel.data.store.StoreType
import org.bson.Document
import kotlin.properties.Delegates

class MongoStoreType<K, V>(
    private val connectionPool: MongoConnectionPool,
    private val serializer: Serializer<V>
) : StoreType<K, V>()
{

    private var collection by Delegates.notNull<MongoCollection<Document>>()

    override fun load(): MongoStoreType<K, V>
    {
        return this.apply {
            this.collection = connectionPool.getCollection(id)
        }
    }

    override fun store(
        key: K,
        value: V
    )
    {
        this.collection.updateOne(
            Filters.eq("_id", key.toString()),
            Document(
                "\$set",
                Document.parse(
                    serializer.serialize(value)!!
                )
            ),
            UpdateOptions().upsert(true)
        )
    }

    override fun retrieve(
        key: K,
        action: ((V) -> Unit)?
    ): V?
    {
        val document = this.collection.find(Filters.eq("_id", key.toString())).first()

        if (document != null)
        {
            val data = serializer.deserialize(document.toJson())

            if (data != null)
            {
                return data.apply {
                    action?.invoke(data)
                }
            }
        }

        return null
    }

    override fun retrieveAll(
        action: ((V) -> Unit)?
    ): Collection<V>
    {
        val elements = mutableListOf<V>()

        this.collection.find().forEach {
            val data = serializer.deserialize(it.toJson())

            if (data != null)
            {
                elements += data.apply {
                    action?.invoke(data)
                }
            }
        }

        return elements
    }

    override fun delete(key: K)
    {
        this.collection.deleteMany(Filters.eq("_id", key.toString()))
    }
}