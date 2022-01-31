package io.github.nosequel.data.store

import java.util.concurrent.CompletableFuture
import java.util.concurrent.ForkJoinPool

abstract class StoreType<K, V>
{

    // this MUST be set in order for the StoreType to work properly.
    // if this is not set, all the values will be stored within the same collection.
    var id = "data-store-default"

    abstract fun load(): StoreType<K, V>

    fun store(
        map: Map<K, V>
    )
    {
        map.forEach {
            store(it.key, it.value)
        }
    }

    abstract fun store(
        key: K,
        value: V,
    )

    abstract fun retrieve(
        key: K,
        action: ((V) -> Unit)? = null
    ): V?

    abstract fun retrieveAll(
        action: ((V) -> Unit)? = null
    ): Collection<V>

    fun retrieveAllAsync(
        action: ((V) -> Unit)? = null
    ): CompletableFuture<Collection<V>>
    {
        return CompletableFuture.supplyAsync {
            this.retrieveAll(action)
        }
    }

    fun retrieveAsync(
        key: K,
        action: ((V) -> Unit)? = null
    ): CompletableFuture<V?>
    {
        return CompletableFuture.supplyAsync {
            this.retrieve(key, action)
        }
    }

    fun storeAsync(
        key: K,
        value: V,
    )
    {
        ForkJoinPool.commonPool().run {
            this@StoreType.store(
                key, value
            )
        }
    }

    fun storeAsync(
        map: Map<K, V>
    )
    {
        map.forEach {
            storeAsync(it.key, it.value)
        }
    }
}