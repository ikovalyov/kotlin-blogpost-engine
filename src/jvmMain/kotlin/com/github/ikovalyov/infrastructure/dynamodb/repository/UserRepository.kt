package com.github.ikovalyov.infrastructure.dynamodb.repository

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import com.github.ikovalyov.model.extension.UserExtension.fromDynamoDbMap
import com.github.ikovalyov.model.extension.UserExtension.toDynamoDbMap
import com.github.ikovalyov.model.security.Email
import com.github.ikovalyov.model.security.Password
import com.github.ikovalyov.model.security.ShortString
import com.github.ikovalyov.model.security.User
import jakarta.inject.Singleton
import kotlinx.coroutines.delay
import kotlinx.serialization.ExperimentalSerializationApi
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

@ExperimentalSerializationApi
@Singleton
class UserRepository(
    dynamoDbClient: DynamoDbAsyncClient,
    private val userRoleRepository: UserRoleRepository
) : CrudRepository<User>(dynamoDbClient) {
    companion object {
        const val tableName = "user"
    }

    override val tableName = UserRepository.tableName

    override suspend fun init(): Boolean {
        super.init()
        while (!userRoleRepository.initialized) {
            delay(100)
        }
        userRoleRepository.createDefaultUserRoles()
        val adminRole = userRoleRepository.getByName(UserRoleRepository.adminRoleName)!!
        val admin = User(
            id = uuid4(),
            email = Email(ShortString("test@example.com")),
            loggedIn = false,
            nickname = "admin",
            roles = listOf(adminRole.id),
            password = Password(ShortString("password"))
        )
        return insert(admin)
    }

    suspend fun list(): List<User> {
        return list {
            User.fromDynamoDbMap(it)
        }
    }

    suspend fun insert(item: User): Boolean {
        return insert(item) {
            item.toDynamoDbMap()
        }
    }

    suspend fun update(item: User): Boolean {
        return update(item) {
            item.toDynamoDbMap()
        }
    }

    suspend fun get(id: Uuid): User? {
        return get(id) {
            User.fromDynamoDbMap(it)
        }
    }

    suspend fun getUserByEmail(email: Email): User? {
        val users = list()
        println(users[0].email)

        val emails = find(
            mapOf(":email" to AttributeValue.fromS(email.toString())),
            filterExpression = "email = :email"
        ) {
            User.fromDynamoDbMap(it)
        }
        if (emails.count() > 1) {
            throw IllegalStateException("More than 1 user with email $email in db")
        }
        if (emails.isEmpty()) {
            return null
        }
        return emails.first()
    }
}
