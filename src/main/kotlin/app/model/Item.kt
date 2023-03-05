package app.model

import com.datastax.oss.driver.api.mapper.annotations.PartitionKey
import java.util.UUID

abstract class Item(@PartitionKey open val id: UUID? = null)
