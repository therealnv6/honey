import io.github.nosequel.data.DataHandler
import io.github.nosequel.data.DataStoreType
import io.github.nosequel.data.connection.mongo.NoAuthMongoConnectionPool
import org.junit.jupiter.api.Test

class MongoTypeTest
{
    @Test
    fun createType()
    {
        DataHandler
            .linkTypeToId<Person>("test")
            .withConnectionPool<NoAuthMongoConnectionPool> {
                this.databaseName = "honey"
                this.hostname = "127.0.0.1"
                this.port = 27017
            }

        val type = DataHandler
            .createStoreType<String, Person>(DataStoreType.MONGO)

        type.store(
            "first", Person("Patrick", 16)
        )

        type.retrieve("first") {
            println(it)
        }
    }
}