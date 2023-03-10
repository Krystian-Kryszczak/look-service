package app.storage.cassandra.event

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.SimpleStatement
import com.datastax.oss.driver.api.core.type.DataTypes
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder
import com.datastax.oss.driver.api.querybuilder.schema.CreateTable
import io.micronaut.context.event.StartupEvent
import io.micronaut.runtime.event.annotation.EventListener
import jakarta.inject.Singleton
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Singleton
class CassandraStartup(private val cqlSession: CqlSession) {
    @EventListener
    internal fun onStartupEvent(event: StartupEvent) {
        createUserTable()
        createLookTable()
    }
    private fun createUserTable() = execute(
        SchemaBuilder
            .createTable("user").ifNotExists()
            .withPartitionKey("id", DataTypes.TIMEUUID)
            .withColumn("name", DataTypes.TEXT)
            .withColumn("lastname", DataTypes.TEXT)
            .withColumn("email", DataTypes.TEXT)
            .withColumn("phone_number", DataTypes.TEXT)
            .withColumn("date_of_birth_in_days", DataTypes.INT)
            .withColumn("sex", DataTypes.TINYINT)
            .build()
    )
    private fun createLookTable() = execute(
        getExhibitTableBase("look")
            .withColumn("description", DataTypes.TEXT)
            .withColumn("has_images", DataTypes.BOOLEAN)
            .build()
    )
    private fun getExhibitTableBase(name: String): CreateTable =
        getNamedItemTableBase(name)
            .withColumn("creator_id", DataTypes.TIMEUUID)
            .withColumn("views", DataTypes.INT)
            .withColumn("rating", DataTypes.INT)
            .withColumn("private", DataTypes.BOOLEAN)
    private fun getNamedItemTableBase(name: String): CreateTable =
        getItemTableBase(name)
            .withColumn("name", DataTypes.TEXT)
    private fun getItemTableBase(name: String): CreateTable =
        SchemaBuilder
            .createTable(name).ifNotExists()
            .withPartitionKey("id", DataTypes.TIMEUUID)
    private fun execute(statement: SimpleStatement) {
        cqlSession.execute(statement)
        logger.info("Query \"${statement.query}\" was executed.")
    }
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(CassandraStartup::class.java)
    }
}
