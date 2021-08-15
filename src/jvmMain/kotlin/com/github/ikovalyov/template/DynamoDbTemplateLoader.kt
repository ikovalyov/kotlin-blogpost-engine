package com.github.ikovalyov.template

import com.github.ikovalyov.infrastructure.dynamodb.repository.ConfigurationRepository
import com.github.ikovalyov.infrastructure.dynamodb.repository.TemplateRepository
import com.github.ikovalyov.model.Template
import freemarker.cache.TemplateLoader
import java.io.Reader
import java.io.StringReader
import javax.inject.Singleton
import kotlinx.coroutines.runBlocking

@Singleton
class DynamoDbTemplateLoader(
    private val configurationRepository: ConfigurationRepository,
    private val templateRepository: TemplateRepository
) : TemplateLoader {
  override fun findTemplateSource(name: String): Template? = runBlocking {
    val templateName = configurationRepository.getActiveTemplateName()
    templateName?.let { templateRepository.get(it) }
  }

  override fun getLastModified(templateSource: Any?): Long {
    require(templateSource is Template)
    return templateSource.lastModified.epochSeconds
  }

  override fun getReader(templateSource: Any?, encoding: String?): Reader {
    require(templateSource is Template)
    return StringReader(templateSource.template)
  }

  override fun closeTemplateSource(templateSource: Any?) {}
}
