package com.github.ikovalyov.template

import com.github.ikovalyov.infrastructure.dynamodb.repository.ConfigurationRepository
import com.github.ikovalyov.infrastructure.dynamodb.repository.TemplateRepository
import com.github.ikovalyov.model.Template
import freemarker.cache.TemplateLoader
import jakarta.inject.Singleton
import kotlinx.coroutines.runBlocking
import java.io.Reader
import java.io.StringReader

@Singleton
class DynamoDbTemplateLoader(
    private val configurationRepository: ConfigurationRepository,
    private val templateRepository: TemplateRepository
) : TemplateLoader {
    override fun findTemplateSource(name: String): Template? = runBlocking {
        val templateName = configurationRepository.getActiveTemplateId()
        templateName?.let { templateRepository.get(it) }
    }

    override fun getLastModified(templateSource: Any?): Long {
        require(templateSource is Template)
        return templateSource.lastModified.epochSeconds
    }

    override fun getReader(templateSource: Any?, encoding: String?): Reader {
        require(templateSource is Template)
        return StringReader(templateSource.body)
    }

    override fun closeTemplateSource(templateSource: Any?) {}
}
