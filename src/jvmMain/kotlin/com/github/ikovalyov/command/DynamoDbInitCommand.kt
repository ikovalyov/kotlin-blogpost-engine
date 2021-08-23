package com.github.ikovalyov.command

import com.github.ikovalyov.infrastructure.dynamodb.InitDynamoDbDatabaseInterface
import io.micronaut.configuration.picocli.PicocliRunner
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import picocli.CommandLine

@CommandLine.Command(
    name = "Init dynamo db",
    description = ["This command generates tables in the dynamo db required for app to operate"],
    mixinStandardHelpOptions = true,
    helpCommand = true)
@Singleton
open class DynamoDbInitCommand : Runnable {
  @Inject lateinit var initializers: List<InitDynamoDbDatabaseInterface>
  private val logger = KotlinLogging.logger {}

  companion object {
    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
      @Suppress("SpreadOperator") PicocliRunner.run(DynamoDbInitCommand::class.java, *args)
    }
  }

  override fun run() {
    runBlocking {
      initializers
          .map {
            logger.info { "Processing ${it.javaClass.simpleName}" }
            async { it.init() }
          }
          .awaitAll()
      logger.info("done")
    }
  }
}
