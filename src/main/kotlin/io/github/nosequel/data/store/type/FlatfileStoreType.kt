package io.github.nosequel.data.store.type

import io.github.nosequel.data.connection.flatfile.FlatfileConnectionPool
import io.github.nosequel.data.serializer.Serializer
import io.github.nosequel.data.store.StoreType
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class FlatfileStoreType<K, V>(
    private val connectionPool: FlatfileConnectionPool,
    private val serializer: Serializer<V>
) : StoreType<K, V>()
{
    lateinit var file: File

    override fun load(): StoreType<K, V>
    {
        return this.apply {
            this.file = File(connectionPool.getConnectionPool(), this.id)

            if (!file.exists())
            {
                file.mkdirs()
            }
        }
    }

    override fun store(key: K, value: V)
    {
        val file = File(this.file, key.toString())

        if (!file.exists())
        {
            file.createNewFile()
        }

        val writer = FileWriter(file)
        val data = serializer.serialize(value)

        if (data != null)
        {
            writer.write(data)
        }

        writer.close()
    }

    override fun retrieve(key: K, action: ((V) -> Unit)?): V?
    {
        val file = File(this.file, key.toString())
        val reader = FileReader(file)

        if (!file.exists())
        {
            return null
        }

        val value = serializer.deserialize(reader.readText()) ?: return null

        return value.apply {
            action?.invoke(this)
        }
    }

    override fun retrieveAll(action: ((V) -> Unit)?): Collection<V>
    {
        val files = this.file.listFiles()

        if (files != null)
        {
            return files
                .mapNotNull { file ->
                    val reader = FileReader(file)
                    val value = serializer.deserialize(reader.readText()) ?: return@mapNotNull null

                    return@mapNotNull value.apply {
                        this.let {
                            action?.invoke(it)
                        }
                    }
                }
        }

        return emptyList()
    }
}