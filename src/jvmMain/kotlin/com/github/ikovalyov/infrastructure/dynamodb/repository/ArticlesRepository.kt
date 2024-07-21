package com.github.ikovalyov.infrastructure.dynamodb.repository

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import com.github.ikovalyov.infrastructure.dynamodb.InitDynamoDbDatabaseInterface
import com.github.ikovalyov.infrastructure.dynamodb.converter.ArticleConverter
import com.github.ikovalyov.infrastructure.dynamodb.repository.articles.CommentsRepository
import com.github.ikovalyov.model.Article
import com.github.ikovalyov.model.Comment
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.delay
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient

@Singleton
class ArticlesRepository(dynamoDbClient: DynamoDbAsyncClient) :
    CrudRepository<Article>(dynamoDbClient),
    InitDynamoDbDatabaseInterface {

    @Inject private lateinit var articleConverter: ArticleConverter

    @Inject private lateinit var commentsRepository: CommentsRepository

    @Inject private lateinit var templatesRepository: TemplatesRepository

    @Inject private lateinit var usersRepository: UsersRepository

    @Inject private lateinit var tagsRepository: TagsRepository

    companion object {
        const val TABLE_NAME = "article"
    }

    override val tableName = TABLE_NAME

    override suspend fun init(): Boolean {
        super.init()

        while (!templatesRepository.initialized) {
            delay(500)
        }

        while (!usersRepository.initialized) {
            delay(50)
        }

        while (!tagsRepository.initialized) {
            delay(50)
        }

        // to catch up with dynamo db
        delay(2000)

        val templates = templatesRepository.list()
        val users = usersRepository.list()

        val article = Article(uuid4(), TABLE_NAME, TABLE_NAME, TABLE_NAME, users.first().id, tagsRepository.list().map { it.name }, emptyList(), templates.first().id)
        insert(article)

        val comment = Comment(uuid4(), "Comment", users.first().id, article.id)
        commentsRepository.init()
        return commentsRepository.insert(comment)
    }

    suspend fun list(): List<Article> = list {
        articleConverter.fromDynamoDB(it)
    }

    suspend fun insert(item: Article): Boolean = insert(item) {
        articleConverter.toDynamoDB(it)
    }

    suspend fun update(item: Article): Boolean = update(item) {
        articleConverter.toDynamoDB(it)
    }

    suspend fun get(id: Uuid): Article? = get(id) {
        articleConverter.fromDynamoDB(it)
    }
}
