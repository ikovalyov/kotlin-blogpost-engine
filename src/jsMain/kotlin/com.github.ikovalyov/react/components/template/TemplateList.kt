package com.github.ikovalyov.react.components.template

import com.github.ikovalyov.Api
import com.github.ikovalyov.model.Template
import com.github.ikovalyov.react.components.template.table.Table
import kotlinx.browser.window
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.await
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import kotlinext.js.jsObject
import react.child

external interface TemplatesState : RState {
    var templates: List<Template>?
}

class TemplateList : RComponent<RProps, TemplatesState>() {
    private var templates: List<Template> = emptyList()

    init {
        window.addEventListener(type = "load", callback = {
            GlobalScope.async {
                loadTemplatesList()
            }
        })
    }

    suspend fun loadTemplatesList() {
        val result = window.fetch("http://localhost:8082" + Api.templateUrl).await().text().await()
        val templatesList = Json.decodeFromString(ListSerializer(Template.serializer()), result)
        templates = templatesList
        setState({
            it.templates = templatesList
            it
        })
    }

    override fun RBuilder.render() {
        child(component = Table, props = jsObject {
            templates = state.templates?.map {
                if (it.template.length > 255) {
                    console.info("truncating the template")
                    it.copy(template = it.template.substring(0, 125) + "...")
                } else it
            }?.toTypedArray()
            onDeleteClick = {
                console.info("delete" + it.id)
            }
            onEditClick = {
                console.info("edit" + it.id)
            }
        })
    }
}
