package com.github.ikovalyov

import com.github.ikovalyov.template.DynamoDbTemplateLoader
import io.micronaut.runtime.Micronaut
import io.micronaut.views.freemarker.FreemarkerViewsRendererConfigurationProperties
import mu.KotlinLogging

class MyApp {
    companion object {
        private val logger = KotlinLogging.logger {  }
        @JvmStatic fun main(args: Array<String>) {
            logger.trace { "Starting search-v3..." }

            // Fuel ignores the `Host` header if you don't set this property
            System.setProperty("sun.net.http.allowRestrictedHeaders", "true")

            val ac = Micronaut.build()
                .packages("com.showpad.micronaut")
                .mainClass(MyApp::class.java)
                .start()

            val freeMarkerProperties = ac.getBean(FreemarkerViewsRendererConfigurationProperties::class.java)
            val templateLoader = ac.getBean(DynamoDbTemplateLoader::class.java)
            freeMarkerProperties.templateLoader = templateLoader
        }
    }
}