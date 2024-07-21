package com.github.ikovalyov.infrastructure.dynamodb.repository

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import com.github.ikovalyov.infrastructure.dynamodb.InitDynamoDbDatabaseInterface
import com.github.ikovalyov.infrastructure.dynamodb.repository.users.UserRolesRepository
import com.github.ikovalyov.model.extension.UserExtension.fromDynamoDbMap
import com.github.ikovalyov.model.extension.UserExtension.toDynamoDbMap
import com.github.ikovalyov.model.security.Email
import com.github.ikovalyov.model.security.Password
import com.github.ikovalyov.model.security.ShortString
import com.github.ikovalyov.model.security.User
import jakarta.inject.Inject
import jakarta.inject.Singleton
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

@Singleton
class UsersRepository(dynamoDbClient: DynamoDbAsyncClient) :
    CrudRepository<User>(dynamoDbClient),
    InitDynamoDbDatabaseInterface {
    @Inject
    private lateinit var userRolesRepository: UserRolesRepository

    companion object {
        const val TABLE_NAME = "user"
    }

    override val tableName = TABLE_NAME

    override suspend fun init(): Boolean {
        super.init()

        userRolesRepository.init()

        val adminRole = userRolesRepository.getByName(UserRolesRepository.ADMIN_ROLE_NAME)!!
        val admin = User(
            id = uuid4(),
            email = Email(ShortString("test@example.com")),
            loggedIn = false,
            nickname = "admin",
            roles = listOf(adminRole.id),
            password = Password(ShortString("password")),
        )
        return insert(admin)
    }

    suspend fun list(): List<User> = list {
        User.fromDynamoDbMap(it)
    }

    suspend fun insert(item: User): Boolean = insert(item) {
        item.toDynamoDbMap()
    }

    suspend fun update(item: User): Boolean = update(item) {
        item.toDynamoDbMap()
    }

    suspend fun get(id: Uuid): User? = get(id) {
        User.fromDynamoDbMap(it)
    }

    suspend fun getUserByEmail(email: Email): User? {
        val users = list()
        println(users[0].email)

        val emails = find(
            mapOf(":email" to AttributeValue.fromS(email.toString())),
            filterExpression = "email = :email",
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
