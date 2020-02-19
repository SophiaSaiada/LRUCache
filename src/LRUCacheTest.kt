import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis

internal class LRUCacheTest {
    private data class Student(override val id: String, val fullName: String) : Identifiable<String>

    private val dataSource = object : DataSource<String, Student> {
        override suspend fun fetch(id: String): Student {
            delay(1000)
            return Student(id, "name of student $id")
        }
    }
    private val cache = LRUCache(cacheSize = 2, dataSource = dataSource)

    private fun assertTimeOfRun(studentId: String, elapsedExpectedTimeInSeconds: Int) {
        val actualTimeInMillis = measureTimeMillis {
            runBlocking {
                assertEquals(cache.get(studentId).fullName, "name of student $studentId")
            }
        }
        assertTrue(
            elapsedExpectedTimeInSeconds * 1000L <= actualTimeInMillis &&
                    actualTimeInMillis < (elapsedExpectedTimeInSeconds + 1) * 1000L
        ) {
            "$actualTimeInMillis is not in range [${elapsedExpectedTimeInSeconds * 1000L}, ${(elapsedExpectedTimeInSeconds + 1) * 1000L})"
        }
    }

    @Test
    fun main() {
        assertTimeOfRun("1", 1) // FETCH [] -> [1]
        assertTimeOfRun("1", 0) // CACHE [1] -> [1]
        assertTimeOfRun("2", 1) // FETCH [1] -> [2, 1]
        assertTimeOfRun("2", 0) // CACHE [2, 1] -> [2, 1]
        assertTimeOfRun("1", 0) // CACHE [2, 1] -> [1, 2]
        assertTimeOfRun("3", 1) // FETCH [1, 2] -> [3, 1]
        assertTimeOfRun("1", 0) // CACHE [3, 1] -> [1, 3]
        assertTimeOfRun("3", 0) // CACHE [1, 3] -> [3, 1]
        assertTimeOfRun("2", 1) // FETCH [3, 1] -> [2, 3]
        assertTimeOfRun("3", 0) // CACHE [2, 3] -> [3, 2]
    }
}
