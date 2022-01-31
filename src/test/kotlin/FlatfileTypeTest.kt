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
            .withConnectionPool<FlatfileConnectionPool> {
                this.directory = "data/"
            }

        val type = DataHandler
            .createStoreType<String, Person>(DataStoreType.FLATFILE) {
                this.id = "test"
            }

        type.store(
            "first", Person("Patrick", 16)
        )

        type.store(
            "second", Person("Patrick", 17)
        )

        type.retrieveAll {
            println(it)
        }
    }
}