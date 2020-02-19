import kotlin.collections.HashMap

class LRUCache<K, V : Identifiable<K>>(
    private val cacheSize: Int,
    private val dataSource: DataSource<K, V>
) {
    private val cache = DoublyLinkedList<V>()
    private val idToNode = HashMap<K, DoublyLinkedList.Node<V>?>()

    suspend fun get(id: K): V =
        idToNode[id]?.let { itemNode ->
            // item is in cache
            cache.moveToHead(itemNode).also { newItemNode ->
                // a new node with the same data was created, we should update `idToNode[id]` to point new one
                idToNode[id] = newItemNode
            }
            itemNode.data
        }
            ?: dataSource.fetch(id).also { item ->
                // item is'nt in cache, we should fetch it from `dataSource` and cache it for further use
                cache.append(item).also { itemNode ->
                    idToNode[id] = itemNode
                }
                if (cache.size > cacheSize) {
                    cache.removeLastNode()?.also { removedItemNode ->
                        idToNode[removedItemNode.data.id] = null
                    }
                }
            }
}
