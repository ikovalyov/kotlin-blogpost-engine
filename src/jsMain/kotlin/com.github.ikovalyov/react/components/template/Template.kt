package com.github.ikovalyov.react.components.template

import com.github.ikovalyov.Api
import com.github.ikovalyov.model.Template
import kotlinx.browser.window
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.w3c.fetch.Headers
import org.w3c.fetch.RequestInit
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.setState

external interface TemplateComponentState : RState {
    var currentState: TemplateComponent.State?
    var currentTemplate: Template?
    var templatesList: List<Template>
}

class TemplateComponent : RComponent<RProps, TemplateComponentState>() {
    enum class State {
        LIST,
        VIEW,
        EDIT
    }

    private fun switchToEditStateFuncVar(t: Template) {
        GlobalScope.launch {
            val template = loadTemplate(t.id)
            setState {
                currentState = State.EDIT
                currentTemplate = template
            }
        }
    }

    fun switchToViewStateFunc(t: Template) {
        GlobalScope.launch {
            val template = loadTemplate(t.id)
            setState {
                currentState = State.VIEW
                currentTemplate = template
            }
        }
    }

    suspend fun switchToListViewStateFunc() {
        val result = window.fetch("http://localhost:8082" + Api.templateUrl).await().text().await()
        val templatesList = Json.decodeFromString(ListSerializer(Template.serializer()), result)

        setState {
            currentState = State.LIST
            currentTemplate = null
            this.templatesList = templatesList
        }
    }

    suspend fun deleteItem(t: Template) {
        if (window.confirm("Are you sure you want to delete this item?")) {
            window
                .fetch(
                    "http://localhost:8082" + Api.templateUrl + "/${t.id}",
                    RequestInit(method = "DELETE"))
                .await()
            switchToListViewStateFunc()
        }
    }

    suspend fun submitEditTemplateForm(t: Template) {
        console.info("inside submitEditTemplateForm")
        updateTemplate(t)
        switchToListViewStateFunc()
    }

    private suspend fun updateTemplate(template: Template) {
        val body = Json.encodeToString(Template.serializer(), template)
        val headers = Headers()
        headers.append("Content-Type", "application/json")
        val fetchResult =
            window.fetch(
                "http://localhost:8082" + Api.templateUrl,
                RequestInit(method = "PATCH", headers = headers, body = body))
        val response = fetchResult.await()
        val result = response.text().await()
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
        if (state.currentState == null) {
            state.currentState = State.LIST
            GlobalScope.async { switchToListViewStateFunc() }
        }
        when (state.currentState) {
            State.LIST ->
                child(TemplateList::class) {
                    attrs {
                        this.switchToEditState = ::switchToEditStateFuncVar
                        this.switchToViewState = ::switchToViewStateFunc
                        this.deleteItem = ::deleteItem
                        this.templates = state.templatesList
                    }
                }
            State.EDIT ->
                child(TemplateEdit::class) {
                    attrs {
                        template = state.currentTemplate!!
                        switchToListState = ::switchToListViewStateFunc
                        submitForm = ::submitEditTemplateForm
                    }
                }
            State.VIEW ->
                child(TemplateView::class) {
                    attrs {
                        template = state.currentTemplate!!
                        switchToListState = ::switchToListViewStateFunc
                    }
                }
        }
    }
}
