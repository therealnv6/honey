package io.github.nosequel.data.connection.mongo

import com.mongodb.MongoClient
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import com.mongodb.client.MongoCollection
import io.github.nosequel.data.connection.ConnectionPool
import org.bson.Document

abstract class MongoConnectionPool : ConnectionPool<MongoClient>()
{
    abstract var databaseName: String?

    private var connectionPool: MongoClient? = null

    fun getCollection(
        collection: String
    ): MongoCollection<Document>
    {
        if (this.connectionPool == null)
        {
            this.connectionPool = this.getConnectionPool()
        }

        return this.connectionPool!!
            .getDatabase(this.databaseName!!)
            .getCollection(collection)
    }
}

class NoAuthMongoConnectionPool : MongoConnectionPool()
{
    override var databaseName: String? = null

    var hostname: String? = null
    var port: Int? = null


    override fun getConnectionPool(): MongoClient
    {
        return MongoClient(
            hostname!!,
            port!!
        )
    }
}

class AuthenticatedMongoConnectionPool : MongoConnectionPool()
{
    override var databaseName: String? = null

    var hostname: String? = null
    var port: Int? = null

    var password: String? = null
    var username: String? = null

    override fun getConnectionPool(): MongoClient
    {
        return if (this.password == null)
        {
            MongoClient(
                hostname!!,
                port!!
            )
        } else
        {
            MongoClient(
                ServerAddress(
                    hostname!!,
                    port!!
                ),
                listOf(
                    MongoCredential.createCredential(
                        this.username!!,
                        this.databaseName!!,
                        this.password!!.toCharArray()
                    )
                )
            )
        }
    }
}