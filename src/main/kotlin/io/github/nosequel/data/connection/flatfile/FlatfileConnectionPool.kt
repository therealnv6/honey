package io.github.nosequel.data.connection.flatfile

import io.github.nosequel.data.connection.ConnectionPool
import java.io.File
import java.lang.IllegalStateException

class FlatfileConnectionPool : ConnectionPool<File>()
{
    var directory: String? = null

    override fun getConnectionPool(): File
    {
        if (this.directory == null)
        {
            throw IllegalStateException("directory is null")
        }

        return File(
            directory!!
        ).apply {
            if (!this.exists())
            {
                this.mkdirs()
            }
        }
    }
}