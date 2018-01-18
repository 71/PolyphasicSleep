package `is`.gregoirege.polyphasicsleep

import org.jetbrains.anko.db.RowParser

class NapDefinition(val hour: Int, val minute: Int, val durationInMinutes: Int)

class NapParser : RowParser<NapDefinition> {
    override fun parseRow(columns: Array<Any?>): NapDefinition {
        val totalMinutes = columns[0] as Int
        val duration = columns[1] as Int

        return NapDefinition(totalMinutes / 60, totalMinutes % 60, duration)
    }
}
