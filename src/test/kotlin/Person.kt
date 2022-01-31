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