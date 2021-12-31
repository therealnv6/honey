package io.github.nosequel.data.connection

abstract class ConnectionPool<T>
{
    abstract fun getConnectionPool(): T
}