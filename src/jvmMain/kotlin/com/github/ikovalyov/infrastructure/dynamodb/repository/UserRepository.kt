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
}
