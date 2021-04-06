package com.github.ikovalyov.infrastructure

import io.micronaut.context.annotation.Factory
import java.net.URI
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient

@Factory
class DynamoDbClientFactory {
    @Factory
    fun createDynamoDbClient(): DynamoDbAsyncClient {
        return DynamoDbAsyncClient.builder()
            .endpointOverride(URI("http://localhost:4566"))
            .credentialsProvider {
                AwsBasicCredentials.create("test", "test")
            }.build()
    }
}