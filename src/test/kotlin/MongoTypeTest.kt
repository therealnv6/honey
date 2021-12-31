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
            .withConnectionPool<NoAuthMongoConnectionPool> {
                this.databaseName = "honey"
                this.hostname = "127.0.0.1"
                this.port = 27017
            }

        val type = DataHandler
            .createStoreType<String, Person>(DataStoreType.MONGO) {
                this.id = "test"
            }

        type.store(
            "first", Person("Patrick", 16)
        )

        type.retrieve("first") {
            println(it)
        }
    }
}

class Person(
    private val name: String,
    private val age: Int
)
{
    override fun toString(): String
    {
        return "$name, $age"
    }
}