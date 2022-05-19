package trash

class EventCalendar {
    private val events = sortedMapOf<Double, MutableList<Event>>()
    var currentTime = 0.0
        private set

    fun scheduleEvent(dt: Double, block: () -> Unit) {
        val time = currentTime + dt
        events.getOrPut(time) { mutableListOf() }.add(Event(time, block))
    }

    fun update(dt: Double) {
        val time = currentTime + dt
        while (events.firstKey() <= time) {
            val key = events.firstKey()
            currentTime = key
            val eventQueue = events[key]!!
            while (eventQueue.isNotEmpty()) {
                val event = eventQueue.first()
                event.block()
                eventQueue.remove(event)
            }
            events -= key
        }
        currentTime = time
    }

    fun clear() {
        events.clear()
    }

    private data class Event(val t: Double, val block: () -> Unit)
}