package com.github.ikovalyov.react.components.template

import com.benasher44.uuid.Uuid
import com.github.ikovalyov.Api
import com.github.ikovalyov.model.Template
import kotlinx.browser.window
import kotlinx.coroutines.DelicateCoroutinesApi
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
import react.PropsWithChildren
import react.State
import react.setState

external interface TemplateComponentState : State {
  var currentState: TemplateComponent.State?
  var currentTemplate: Template?
  var templatesList: List<Template>
}

@OptIn(DelicateCoroutinesApi::class)
class TemplateComponent : RComponent<PropsWithChildren, TemplateComponentState>() {
  enum class State {
    LIST,
    VIEW,
    EDIT,
    ADD
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

  private fun switchToViewStateFunc(t: Template) {
    GlobalScope.launch {
      val template = loadTemplate(t.id)
      setState {
        currentState = State.VIEW
        currentTemplate = template
      }
    }
  }

  private fun switchToAddStateFunc() {
    GlobalScope.launch {
      setState {
        currentState = State.ADD
        currentTemplate = null
      }
    }
  }

  private suspend fun switchToListViewStateFunc() {
    val result = window.fetch("http://localhost:8082" + Api.templateUrl).await().text().await()
    val templatesList = Json.decodeFromString(ListSerializer(Template.serializer()), result)

    setState {
      currentState = State.LIST
      currentTemplate = null
      this.templatesList = templatesList
    }
  }

  fun switchToInsertStateFunc() {
    setState {
      currentState = State.ADD
      currentTemplate = null
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

  suspend fun submitInsertTemplateForm(t: Template) {
    console.info("inside submitInsertTemplateForm")
    insertTemplate(t)
    switchToListViewStateFunc()
  }

  private suspend fun insertTemplate(template: Template) {
    val body = Json.encodeToString(Template.serializer(), template)
    val headers = Headers()
    headers.append("Content-Type", "application/json")
    val fetchResult =
        window.fetch(
            "http://localhost:8082" + Api.templateUrl,
            RequestInit(method = "POST", headers = headers, body = body))
    val response = fetchResult.await()
    val result = response.text().await()
    console.info(result)
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
    response.text().await()
  }

  private suspend fun loadTemplate(templateId: Uuid): Template {
    val result =
        window
            .fetch("http://localhost:8082" + Api.templateUrl + "/${templateId.toString()}")
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
              this.switchToInsertState = ::switchToInsertStateFunc
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
      State.ADD ->
          child(TemplateInsert::class) {
            attrs {
              switchToListState = ::switchToListViewStateFunc
              submitForm = ::submitInsertTemplateForm
            }
          }
    }
  }
}
