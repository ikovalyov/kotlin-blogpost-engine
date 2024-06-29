package com.github.ikovalyov.application.api

import com.benasher44.uuid.uuidFrom
import com.github.ikovalyov.Api
import com.github.ikovalyov.model.Item
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Consumes
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import kotlinx.datetime.Clock
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest

@Controller(Api.RECORDS_API_URL)
@Secured(SecurityRule.IS_AUTHENTICATED)
class RecordsController(private val client: DynamoDbAsyncClient) {
    @Get
    fun get(): String = "success"

    @Get("{id}")
    fun getItem(id: Int): HttpResponse<Item> {
        val keyToGet = mapOf("id" to AttributeValue.builder().s(id.toString()).build())
        val request = GetItemRequest.builder().key(keyToGet).tableName("record").build()
        val response = client.getItem(request).get()
        return if (response.hasItem()) {
            val itemId = response.item()["id"]!!.s()
            val uuid = uuidFrom(itemId)
            val item = Item(id = uuid, body = response.item()["body"]!!.s(), lastModified = Clock.System.now())
            HttpResponse.ok(item)
        } else {
            HttpResponse.notFound()
        }
    }

    @Post("/")
    @Consumes(MediaType.APPLICATION_JSON)
    fun insertItem(@Body item: Item): HttpResponse<*> {
        val request =
            PutItemRequest.builder()
                .tableName("record")
                .item(
                    mutableMapOf(
                        "id" to AttributeValue.builder().s(item.id.toString()).build(),
                        "body" to AttributeValue.builder().s(item.body).build(),
                    ),
                )
                .build()
        val response = client.putItem(request).get()
        return HttpResponse.status<Nothing>(HttpStatus.valueOf(response.sdkHttpResponse().statusCode()))
    }
}
