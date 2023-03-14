package app.service.being.user

import app.model.being.user.User
import com.datastax.oss.driver.api.core.uuid.Uuids
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.micronaut.test.extensions.kotest.annotation.MicronautTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@MicronautTest
class UserServiceTest(private val userService: UserService): StringSpec({
    val testUser = User(
        Uuids.timeBased(),
        "John",
        "Smith",
        "john.smith@example.com",
        "111 022 342",
        0,
        1
    )

    beforeEach {
        withContext(Dispatchers.IO) {
            userService.saveReactive(testUser)
                .doOnComplete {
                    println("The test user has been saved in the database!")
                }
                .blockingSubscribe()
        }
    }

    "find by id should return null" {
        withContext(Dispatchers.IO) {
            userService.delete(testUser)
                .andThen(userService.findByIdReactive(testUser.id!!))
                .blockingGet()
        } shouldBe null
    }

    "save user should not throw error" {
        withContext(Dispatchers.IO) {
            userService.saveReactive(testUser)
                .blockingSubscribe()
        }
    }

    "user found by id should be equals test user" {
        withContext(Dispatchers.IO) {
            userService.findByIdReactive(testUser.id!!)
                .blockingGet()
        } shouldBe testUser
    }

    "user found by email should be equals test user" {
        withContext(Dispatchers.IO) {
            userService.findByEmailReactive(testUser.email!!)
                .blockingGet()
        } shouldBe testUser
    }

    "user found by id should be equals modifiedUser" {
        val modifiedUser = User(
            testUser.id,
            "John",
            "Smith",
            "john.smith@example.com",
            "111 022 300",
            0,
            1
        )
        withContext(Dispatchers.IO) {
            userService.update(modifiedUser)
                .andThen(userService.findByIdReactive(testUser.id!!))
                .blockingGet()
        } shouldBe modifiedUser
    }

    afterEach {
        withContext(Dispatchers.IO) {
            userService.deleteByIdReactive(testUser.id!!)
                .doOnComplete {
                    println("The test user has been deleted from the database!")
                }
                .blockingSubscribe()
        }
    }
})
