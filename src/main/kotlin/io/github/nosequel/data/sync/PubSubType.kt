package io.github.nosequel.data.sync

import io.github.nosequel.data.DataHandler

abstract class PubSubType<T>
{
    var id = DataHandler.DEFAULT_ID
    var handle: (T) -> Unit = {}

    abstract fun load()

    abstract fun incoming(message: String)
    abstract fun publish(value: T)

    fun handle(value: T)
    {
        this.handle.invoke(value)
    }
}