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
        const val TABLE_NAME = "userRole"
        const val ADMIN_ROLE_NAME = "admin"
        const val USER_ROLE_NAME = "user"
        const val GUEST_USER_ROLE_NAME = "guest"
        const val FIELD_NAME = "name"
    }

    override val tableName = TABLE_NAME

    suspend fun createDefaultUserRoles(): Boolean {
        val adminRoleFromDb = getByName(ADMIN_ROLE_NAME)
        val userRole = UserRole(uuid4(), Clock.System.now(), USER_ROLE_NAME)
        val guestRole = UserRole(uuid4(), Clock.System.now(), GUEST_USER_ROLE_NAME)
        val adminInsertResult = if (adminRoleFromDb == null) {
            createAdmin()
        } else {
            true
        }
        return adminInsertResult && insert(userRole) && insert(guestRole)
    }

    suspend fun createAdmin(): Boolean {
        val admin = UserRole(uuid4(), Clock.System.now(), ADMIN_ROLE_NAME)
        return insert(admin)
    }

    suspend fun list(): List<UserRole> = list {
        UserRole.fromDynamoDbMap(it)
    }

    suspend fun insert(item: UserRole): Boolean = insert(item) {
        item.toDynamoDbMap()
    }

    suspend fun update(item: UserRole): Boolean = update(item) {
        item.toDynamoDbMap()
    }

    suspend fun get(id: Uuid): UserRole? = get(id) {
        UserRole.fromDynamoDbMap(it)
    }

    suspend fun getByName(name: String): UserRole? {
        val response = dynamoDbClient.scan {
            it.tableName(tableName)
            it.expressionAttributeValues(mutableMapOf(Pair(":nameParam", AttributeValue.builder().s(name).build())))
            it.filterExpression("#keyone = :nameParam")
            it.expressionAttributeNames(
                mutableMapOf(
                    Pair("#keyone", FIELD_NAME),
                ),
            )
        }.await()
        if (!response.hasItems() || response.items().isEmpty()) return null
        return UserRole.fromDynamoDbMap(response.items()[0])
    }
}
