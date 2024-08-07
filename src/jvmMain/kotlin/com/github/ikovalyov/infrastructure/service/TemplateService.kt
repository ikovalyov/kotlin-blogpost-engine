package com.github.ikovalyov.infrastructure.service

import com.github.ikovalyov.infrastructure.dynamodb.repository.TemplatesRepository
import com.github.ikovalyov.model.Template
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class TemplateService {
    @Inject
    private lateinit var templatesRepository: TemplatesRepository

    suspend fun getAllTemplates(): List<Template> = templatesRepository.list()
}
