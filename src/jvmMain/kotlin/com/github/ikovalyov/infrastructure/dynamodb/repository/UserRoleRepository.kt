package com.github.ikovalyov.infrastructure.dynamodb.repository

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import com.github.ikovalyov.model.extension.UserRoleExtension.fromDynamoDbMap
import com.github.ikovalyov.model.extension.UserRoleExtension.toDynamoDbMap
import com.github.ikovalyov.model.security.UserRole
import jakarta.inject.Singleton
import kotlinx.datetime.Clock
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient

@Singleton
class UserRoleRepository(dynamoDbClient: DynamoDbAsyncClient) : CrudRepository<UserRole>(dynamoDbClient) {
    companion object {
        const val tableName = "userRole"
        const val adminRoleName = "admin"
        const val userRoleName = "user"
        const val guestRoleName = "guest"
    }

    override val tableName = UserRoleRepository.tableName

    override suspend fun init(): Boolean {
        super.init()
        val adminRole = UserRole(uuid4(), Clock.System.now(), adminRoleName)
        val userRole = UserRole(uuid4(), Clock.System.now(), userRoleName)
        val guestRole = UserRole(uuid4(), Clock.System.now(), guestRoleName)
        return insert(adminRole) && insert(userRole) && insert(guestRole)
    }

    suspend fun list(): List<UserRole> {
        return list {
            UserRole.fromDynamoDbMap(it)
        }
    }

    suspend fun insert(item: UserRole): Boolean {
        return insert(item) {
            item.toDynamoDbMap()
        }
    }

    suspend fun update(item: UserRole): Boolean {
        return update(item) {
            item.toDynamoDbMap()
        }
    }

    suspend fun get(id: Uuid): UserRole? {
        return get(id) {
            UserRole.fromDynamoDbMap(it)
        }
    }
}
