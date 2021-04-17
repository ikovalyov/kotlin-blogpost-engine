package com.github.ikovalyov.template

import com.github.ikovalyov.infrastructure.dynamodb.repository.ConfigurationRepository
import com.github.ikovalyov.infrastructure.dynamodb.repository.TemplatesRepository
import com.github.ikovalyov.model.Template
import freemarker.cache.TemplateLoader
import java.io.Reader
import java.io.StringReader
import javax.inject.Singleton
import kotlinx.coroutines.runBlocking

@Singleton
class DynamoDbTemplateLoader(
    private val configurationRepository: ConfigurationRepository,
    private val templatesRepository: TemplatesRepository
) : TemplateLoader {
    override fun findTemplateSource(name: String): Template? =
        runBlocking {
            val templateName = configurationRepository.getActiveTemplateName()!!
            templatesRepository.getTemplate(templateName)
        }

    override fun getLastModified(templateSource: Any?): Long {
        require(templateSource is Template)
        return templateSource.lastModified.epochSecond
    }

    override fun getReader(templateSource: Any?, encoding: String?): Reader {
        require(templateSource is Template)
        return StringReader(templateSource.template)
    }

    override fun closeTemplateSource(templateSource: Any?) {}
}
