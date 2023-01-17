package com.github.ikovalyov.infrastructure.service

import com.github.ikovalyov.infrastructure.dynamodb.repository.TemplateRepository
import com.github.ikovalyov.model.Template
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class TemplateService {
    @Inject
    private lateinit var templateRepository: TemplateRepository

    suspend fun getAllTemplates(): List<Template> {
        return templateRepository.list()
    }
}