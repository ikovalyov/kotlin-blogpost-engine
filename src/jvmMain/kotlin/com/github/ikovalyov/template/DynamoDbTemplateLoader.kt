package com.github.ikovalyov.template

import com.github.ikovalyov.infrastructure.dynamodb.repository.ConfigurationsRepository
import com.github.ikovalyov.infrastructure.dynamodb.repository.TemplatesRepository
import com.github.ikovalyov.model.Template
import freemarker.cache.TemplateLoader
import jakarta.inject.Singleton
import kotlinx.coroutines.runBlocking
import java.io.Reader
import java.io.StringReader

@Singleton
class DynamoDbTemplateLoader(private val configurationsRepository: ConfigurationsRepository, private val templatesRepository: TemplatesRepository) : TemplateLoader {
    override fun findTemplateSource(name: String): Template? = runBlocking {
        val templateName = configurationsRepository.getActiveTemplateId()
        templateName?.let { templatesRepository.get(it) }
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
