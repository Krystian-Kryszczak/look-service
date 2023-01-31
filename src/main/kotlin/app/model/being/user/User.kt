package app.model.being.user

import app.model.being.Being
import com.datastax.oss.driver.api.mapper.annotations.*
import java.util.UUID

@Entity
@SchemaHint(targetElement = SchemaHint.TargetElement.TABLE)
data class User(
    @PartitionKey
    override var id: UUID? = null,
    override var name: String? = null,
    var lastname: String? = null,
    var email: String? = null,
    var phoneNumber: String? = null,
    var password: String? = null,
    var dateOfBirth: Int = 0,
    var sex: Byte = 0
): Being(id, name)
