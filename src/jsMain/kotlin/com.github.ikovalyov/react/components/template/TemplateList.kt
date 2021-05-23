package com.github.ikovalyov.react.components.template

import com.github.ikovalyov.Api
import com.github.ikovalyov.model.Template
import com.github.ikovalyov.react.components.template.table.Table
import kotlinext.js.jsObject
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
import react.child

external interface TemplateListState : RState {
    var templates: List<Template>?
}

external interface TemplateListProps : RProps {
    var switchToViewState: (Template) -> Unit
    var switchToEditState: (Template) -> Unit
}

class TemplateList : RComponent<TemplateListProps, TemplateListState>() {
    private var templates: List<Template> = emptyList()

    init {
        GlobalScope.async { loadTemplatesList() }
    }

    private suspend fun loadTemplatesList() {
        val result = window.fetch("http://localhost:8082" + Api.templateUrl).await().text().await()
        val templatesList = Json.decodeFromString(ListSerializer(Template.serializer()), result)
        templates = templatesList
        setState({
            it.templates = templatesList
            it
        })
    }

    override fun RBuilder.render() {
        console.info(props)
        child(
            component = Table,
            props =
                jsObject {
                    templates =
                        state
                            .templates
                            ?.map {
                                if (it.template.length > 255) {
                                    it.copy(template = it.template.substring(0, 125) + "...")
                                } else it
                            }
                            ?.toTypedArray()
                    onViewClick = { props.switchToViewState(it) }
                    onEditClick = { props.switchToEditState(it) }
                })
    }
}
