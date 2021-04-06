package com.github.ikovalyov

import io.micronaut.runtime.Micronaut
import mu.KotlinLogging

class MyApp {
    companion object {
        private val logger = KotlinLogging.logger {  }
        @JvmStatic fun main(args: Array<String>) {
            logger.trace { "Starting search-v3..." }

            // Fuel ignores the `Host` header if you don't set this property
            System.setProperty("sun.net.http.allowRestrictedHeaders", "true")

            Micronaut.build()
                .packages("com.showpad.micronaut")
                .mainClass(MyApp::class.java)
                .start()
        }
    }
}