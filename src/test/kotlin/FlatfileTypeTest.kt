import io.github.nosequel.data.DataHandler
import io.github.nosequel.data.DataStoreType
import io.github.nosequel.data.connection.flatfile.FlatfileConnectionPool
import org.junit.jupiter.api.Test

class FlatfileTypeTest
{
    @Test
    fun createType()
    {
        DataHandler
            .linkTypeToId<Person>("test")
            .withConnectionPool<FlatfileConnectionPool> {
                this.directory = "data/"
            }

        val type = DataHandler
            .createStoreType<String, Person>(DataStoreType.FLATFILE)

        type.store(
            mapOf(
                "first" to Person("Patrick", 16),
                "second" to Person("Patrick", 27)
            )
        )

        type.retrieveAll {
            println(it)
        }
    }
}
