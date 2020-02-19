interface Identifiable<K> {
    val id: K
}

interface DataSource<K, V : Identifiable<K>> {
    suspend fun fetch(id: K): V
}
