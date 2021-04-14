package com.github.ikovalyov.infrastructure.dynamodb.factory

import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import java.net.URI
import javax.inject.Singleton
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient

@Factory
class DynamoDbClientFactory(
    @Property(name = "blog.aws.endpoint") val endpoint: String,
    @Property(name = "blog.aws.credentials.username") val username: String,
    @Property(name = "blog.aws.credentials.secretAccessKey") val accessKey: String
) {
    @Singleton
    @Requires(property="blog.aws.localstack", value="true")
    fun createDynamoDbClient(): DynamoDbAsyncClient {
        return DynamoDbAsyncClient.builder()
            .endpointOverride(URI(endpoint))
            .credentialsProvider {
                AwsBasicCredentials.create(username, accessKey)
            }.build()
    }
}