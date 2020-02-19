class DoublyLinkedList<T> {
    class Node<T>(
        val data: T,
        var prev: Node<T>?,
        var next: Node<T>?
    )

    private var head: Node<T>? = null
    private var tail: Node<T>? = null
    var size = 0

    fun append(data: T): Node<T> {
        val newHead = Node(data, prev = null, next = head)
        head?.prev = newHead
        head = newHead
        if (tail == null) {
            tail = newHead
        }
        size++
        return newHead
    }

    fun removeLastNode() =
        tail?.also(::removeNode)

    private fun removeNode(node: Node<T>) {
        node.prev?.next = node.next // update prev's next
        node.next?.prev = node.prev // update next's prev
        if (node == head) { // update `head` if the last node has been removed
            head = node.next
        }
        if (node == tail) { // update `tail` if the last node has been removed
            tail = node.prev
        }
        size--
    }


    fun moveToHead(node: Node<T>) : Node<T> {
        removeNode(node)
        return append(node.data)
    }
}
