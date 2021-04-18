package com.github.ikovalyov.infrastructure.dynamodb.factory

import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Replaces
import io.micronaut.context.annotation.Requires
import java.net.URI
import javax.inject.Singleton
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClientBuilder

@Factory
class DynamoDbClientFactory(
    @Property(name = "blog.aws.endpoint") private val endpoint: String,
    @Property(name = "blog.aws.credentials.username") private val username: String,
    @Property(name = "blog.aws.credentials.secretAccessKey") private val accessKey: String,
    @Property(name = "blog.aws.region", defaultValue = "") private val region: String?,
    private val clientBuilder: DynamoDbAsyncClientBuilder
) {
    @Singleton
    @Requires(property = "blog.aws.localstack", value = "true")
    @Replaces(DynamoDbAsyncClient::class)
    fun createDynamoDbClient(): DynamoDbAsyncClient {
        return clientBuilder
            .endpointOverride(URI(endpoint))
            .credentialsProvider { AwsBasicCredentials.create(username, accessKey) }
            .also {
                if (!region.isNullOrEmpty()) {
                    it.region(Region.of(region))
                }
            }
            .build()
    }
}
