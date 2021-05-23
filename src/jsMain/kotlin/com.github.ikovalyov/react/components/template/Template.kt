package com.github.ikovalyov.react.components.template

import com.github.ikovalyov.Api
import com.github.ikovalyov.model.Template
import kotlinx.browser.window
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.setState

external interface TemplateComponentState : RState {
    var currentState: TemplateComponent.State?
    var currentTemplate: Template?
}

class TemplateComponent : RComponent<RProps, TemplateComponentState>() {
    enum class State {
        LIST,
        VIEW,
        EDIT
    }

    init {
        state.currentState = state.currentState ?: State.LIST
    }

    private val switchToEditStateFuncVar =
        { t: Template ->
            setState {
                currentState = State.EDIT
                currentTemplate = t
            }
        }

    private val switchToViewStateFuncVar =
        { t: Template ->
            GlobalScope.launch {
                val template = loadTemplate(t.id)
                setState {
                    currentState = State.VIEW
                    currentTemplate = template
                }
            }
            Unit
        }

    val switchToListViewStateFuncVar =
        {
            console.info("switch back to list view")
            setState {
                currentState = State.LIST
                currentTemplate = null
            }
        }

    private suspend fun loadTemplate(templateId: String): Template {
        val result =
            window
                .fetch("http://localhost:8082" + Api.templateUrl + "/$templateId")
                .await()
                .text()
                .await()
        return Json.decodeFromString(Template.serializer(), result)
    }

    override fun RBuilder.render() {
        console.info("rerendering the Template component")
        when (state.currentState) {
            State.LIST ->
                child(TemplateList::class) {
                    attrs {
                        this.switchToEditState = switchToEditStateFuncVar
                        this.switchToViewState = switchToViewStateFuncVar
                    }
                }
            State.EDIT ->
                child(TemplateEdit::class) { attrs { template = state.currentTemplate!! } }
            State.VIEW ->
                child(TemplateView::class) {
                    attrs {
                        template = state.currentTemplate!!
                        switchToListState = switchToListViewStateFuncVar
                    }
                }
        }
    }
}
