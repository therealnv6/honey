import io.github.nosequel.data.DataHandler
import io.github.nosequel.data.DataStoreType
import io.github.nosequel.data.connection.redis.NoAuthRedisConnectionPool
import org.junit.jupiter.api.Test

class RedisPubSubTypeTest
{
    @Test
    fun pubSubTest()
    {
        DataHandler
            .linkTypeToId<Person>("redis-test")
            .withConnectionPool<NoAuthRedisConnectionPool> {
                this.hostname = "127.0.0.1"
                this.port = 6379
            }

        DataHandler.createPubSubType<Person>(DataStoreType.REDIS) {
            println("retrieved $it")
        }

        DataHandler.publish(
            Person("rawr", 16)
        )

        Thread.sleep(1500)
    }
}