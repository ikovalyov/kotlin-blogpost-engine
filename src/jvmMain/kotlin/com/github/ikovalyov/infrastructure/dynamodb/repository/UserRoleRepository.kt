package com.github.ikovalyov.infrastructure.dynamodb.repository

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import com.github.ikovalyov.model.extension.UserRoleExtension.fromDynamoDbMap
import com.github.ikovalyov.model.extension.UserRoleExtension.toDynamoDbMap
import com.github.ikovalyov.model.security.UserRole
import jakarta.inject.Singleton
import kotlinx.coroutines.future.await
import kotlinx.datetime.Clock
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

@Singleton
class UserRoleRepository(dynamoDbClient: DynamoDbAsyncClient) : CrudRepository<UserRole>(dynamoDbClient) {

    companion object {
        const val tableName = "userRole"
        const val adminRoleName = "admin"
        const val userRoleName = "user"
        const val guestRoleName = "guest"
        const val fieldName = "name"
    }

    override val tableName = UserRoleRepository.tableName

    suspend fun createDefaultUserRoles(): Boolean {
        val adminRoleFromDb = getByName(adminRoleName)
        val userRole = UserRole(uuid4(), Clock.System.now(), userRoleName)
        val guestRole = UserRole(uuid4(), Clock.System.now(), guestRoleName)
        val adminInsertResult = if (adminRoleFromDb == null) {
            createAdmin()
        } else {
            true
        }
        return adminInsertResult && insert(userRole) && insert(guestRole)
    }

    suspend fun createAdmin(): Boolean {
        val admin = UserRole(uuid4(), Clock.System.now(), adminRoleName)
        return insert(admin)
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

    suspend fun getByName(name: String): UserRole? {
        val response = dynamoDbClient.scan {
            it.tableName(tableName)
            it.expressionAttributeValues(mutableMapOf(Pair(":nameParam", AttributeValue.builder().s(name).build())))
            it.filterExpression("#keyone = :nameParam")
            it.expressionAttributeNames(
                mutableMapOf(
                    Pair("#keyone", fieldName)
                )
            )
        }.await()
        if (!response.hasItems() || response.items().isEmpty()) return null
        return UserRole.fromDynamoDbMap(response.items()[0])
    }
}
