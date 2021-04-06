package com.github.ikovalyov

import com.github.ikovalyov.model.Item
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Consumes
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse

@Controller("/")
class RecordsController(private val client: DynamoDbAsyncClient) {
    /**
    awslocal dynamodb create-table \
    --table-name record \
    --attribute-definitions AttributeName=id,AttributeType=S \
    --key-schema AttributeName=id,KeyType=HASH \
    --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 \
    --tags Key=Owner,Value=ik
     */


    @Get()
    fun get(): String {
        return "success"
    }

    @Get("{id}")
    fun getItem(id: Int): HttpResponse<Item> {
        val keyToGet = mapOf(
            "id" to AttributeValue.builder().s(id.toString()).build()
        )
        val request = GetItemRequest.builder()
            .key(keyToGet)
            .tableName("record")
            .build()
        val response = client.getItem(request).get()
        return if (response.hasItem()) {
            val item = Item(
                id = response.item()["id"]!!.s().toInt(),
                content = response.item()["body"]!!.s()
            )
            HttpResponse.ok(
                item
            )
        } else {
            HttpResponse.notFound()
        }
    }

    @Post("/")
    @Consumes(MediaType.APPLICATION_JSON)
    fun insertItem(@Body item: Item): HttpResponse<*> {
        val request =  PutItemRequest.builder()
            .tableName("record")
            .item(mutableMapOf(
                "id" to AttributeValue.builder().s(item.id.toString()).build(),
                "body" to AttributeValue.builder().s(item.content).build()
            ))
            .build()
        val response = client.putItem(request).get()
        return HttpResponse.status<Nothing>(HttpStatus.valueOf(response.sdkHttpResponse().statusCode()))
    }
}