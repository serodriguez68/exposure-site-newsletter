package support

import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException
import java.util.Locale

/* FIXME: NOT IN USE YET
*  This SQL based cleanup strategy is based on https://miensol.pl/clear-database-in-spring-boot-tests/
*  It was written from MySQL, so we still need to see if it works in Postgres.
*  This is meant to replace the aggressive Flyway based cleanup strategy.
* */

class DatabaseCleaner(private val connectionProvider: () -> Connection) {
    private val tablesToExclude = mutableSetOf<String>()
    private var tablesForClearing: List<TableRef>? = null

    fun excludeTables(vararg tableNames: String) {
        tablesToExclude += tableNames.flatMap { listOf(it, it.lowercase(Locale.getDefault())) }
    }

    fun reset() {
        if (notPrepared) {
            prepare()
        }
        executeReset()
    }

    private val notPrepared get() = tablesForClearing == null

    private fun prepare() {
        connectionProvider().use { connection ->
            val metaData = connection.metaData
            val tableRefs = metaData.getTables(connection.catalog, null, null, arrayOf("TABLE")).use { tables ->
                iterator(tables::next) { tables.getString("TABLE_NAME") }
                    .asSequence()
                    .filterNot(tablesToExclude::contains)
                    .map(::TableRef)
                    .toList()
            }

            tablesForClearing = tableRefs

            LOG.info("Prepared clean db command: {}", tablesForClearing)
        }
    }

    private fun executeReset() {
        try {
            connectionProvider().use { connection ->
                val reset = buildClearStatement(connection)
                val result = reset.executeBatch()
                result
            }
        } catch (e: SQLException) {
            val status = engineInnoDbStatus()
            LOG.error("Failed to remove rows because {}. InnoDb status: {}", e, status)
            throw e
        }
    }

    private fun engineInnoDbStatus(): String {
        return connectionProvider().use { connection ->
            connection.createStatement().executeQuery("SHOW ENGINE INNODB STATUS ").use {
                iterator(it::next) { it.getString("Status") }.asSequence().toList()
            }.joinToString(System.lineSeparator())
        }
    }

    private fun buildClearStatement(connection: Connection): PreparedStatement {
        val reset = connection.prepareStatement("")
        reset.addBatch("SET FOREIGN_KEY_CHECKS = 0")
        tablesForClearing?.forEach { ref ->
            reset.addBatch("DELETE FROM `${ref.name}`")
        }
        reset.addBatch("SET FOREIGN_KEY_CHECKS = 1")
        return reset
    }

    data class TableRef(val name: String)

    companion object {
        private val LOG = LoggerFactory.getLogger(DatabaseCleaner::class.java)!!
    }
}

inline fun <T> iterator(crossinline next: () -> Boolean, crossinline value: () -> T): AbstractIterator<out T> = object : AbstractIterator<T>() {
    override fun computeNext() {
        if (next()) {
            setNext(value())
        } else {
            done()
        }
    }
}