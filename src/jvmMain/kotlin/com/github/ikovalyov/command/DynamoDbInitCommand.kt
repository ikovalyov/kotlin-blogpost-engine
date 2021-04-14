package com.github.ikovalyov.command

import io.micronaut.configuration.picocli.PicocliRunner
import java.util.concurrent.CompletableFuture
import javax.inject.Inject
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.future.asDeferred
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import picocli.CommandLine
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition
import software.amazon.awssdk.services.dynamodb.model.CreateTableResponse
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement
import software.amazon.awssdk.services.dynamodb.model.KeyType
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType
import software.amazon.awssdk.services.dynamodb.model.Tag

@CommandLine.Command(
    name = "Init dynamo db",
    description = ["This command generates tables in the dynamo db required for app to operate"],
    mixinStandardHelpOptions = true,
    helpCommand = true
)
open class DynamoDbInitCommand: Runnable {
    @Inject lateinit var dynamoDbClient: DynamoDbAsyncClient
    private val logger = KotlinLogging.logger {  }

    companion object {
        const val recordTableName = "record"
        const val templateTableName = "template"

        @Throws(Exception::class)
        @JvmStatic
        fun main(args: Array<String>) {
            @Suppress("SpreadOperator")
            PicocliRunner.run(DynamoDbInitCommand::class.java, *args)
        }
    }

    override fun run() {
        runBlocking {
            try {
                listOf(
                    createRecordsTable().asDeferred(),
                    createTemplateTable().asDeferred()
                ).awaitAll()
            } catch (t: ResourceInUseException) {
                logger.error(t) { "Table already exists" }
                emptyList<CreateTableResponse>()
            }
            logger.info("done")
        }
    }

    private fun createTemplateTable(): CompletableFuture<CreateTableResponse> {
        logger.info { "creating $templateTableName table" }
        return dynamoDbClient.createTable {
            it.tableName(templateTableName)
            it.attributeDefinitions(
                AttributeDefinition.builder()
                    .attributeName("id")
                    .attributeType(ScalarAttributeType.S)
                    .build()
            )
            it.keySchema(
                KeySchemaElement.builder()
                    .attributeName("id")
                    .keyType(KeyType.HASH)
                    .build()
            )
            it.provisionedThroughput { builder ->
                builder.readCapacityUnits(5)
                    .writeCapacityUnits(5)
                    .build()
            }
            it.tags(
                listOf(
                    Tag.builder().key("Owner").value("Ik").build()
                )
            )
        }
    }

    private fun createRecordsTable(): CompletableFuture<CreateTableResponse> {
        logger.info { "creating $recordTableName table" }
        return dynamoDbClient.createTable {
            it.tableName(recordTableName)
            it.attributeDefinitions(
                AttributeDefinition.builder()
                    .attributeName("id")
                    .attributeType(ScalarAttributeType.S)
                    .build()
            )
            it.keySchema(
                KeySchemaElement.builder()
                    .attributeName("id")
                    .keyType(KeyType.HASH)
                    .build()
            )
            it.provisionedThroughput { builder ->
                builder.readCapacityUnits(5)
                    .writeCapacityUnits(5)
                    .build()
            }
            it.tags(
                listOf(
                    Tag.builder().key("Owner").value("Ik").build()
                )
            )
        }
    }
}