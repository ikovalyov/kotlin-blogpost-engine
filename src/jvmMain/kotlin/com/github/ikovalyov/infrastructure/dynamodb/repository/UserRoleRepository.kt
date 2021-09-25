package com.github.ikovalyov.infrastructure.dynamodb.repository

import com.benasher44.uuid.Uuid
import com.github.ikovalyov.model.extension.UserRoleExtension.fromDynamoDbMap
import com.github.ikovalyov.model.extension.UserRoleExtension.toDynamoDbMap
import com.github.ikovalyov.model.security.UserRole
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient

class UserRoleRepository(dynamoDbClient: DynamoDbAsyncClient) : CrudRepository<UserRole>(dynamoDbClient) {
    companion object {
        const val tableName = "userRole"
    }

    override val tableName = UserRoleRepository.tableName

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
