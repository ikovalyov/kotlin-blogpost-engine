package com.github.ikovalyov.react.components.template

import com.benasher44.uuid.Uuid
import com.github.ikovalyov.model.markers.IEditable
import com.github.ikovalyov.react.components.template.table.Button
import com.github.ikovalyov.react.components.template.table.ButtonProps
import kotlinext.js.jsObject
import kotlinx.browser.window
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import org.w3c.fetch.Headers
import org.w3c.fetch.RequestInit
import react.FC
import react.PropsWithChildren
import react.RBuilder
import react.RComponent
import react.State
import react.fc
import react.setState
import react.useState

external interface CrudComponentProps<T> : PropsWithChildren {
  var decodeItem: (String) -> T
  var decodeItems: (String) -> List<T>
  var apiUri: String
}

external interface CrudComponentState<T> : State {
  var currentState: CrudState?
  var currentItem: T?
  var itemsList: List<T>
}

enum class CrudState {
  LIST,
  VIEW,
  EDIT,
  ADD
}

@DelicateCoroutinesApi
private fun <T: IEditable<T>> RBuilder.CrudComponent(props: CrudComponentProps<T>) {
  val stateInstance = useState<CrudComponentState<T>> {
    jsObject()
  }
  val updateState = stateInstance.component2()
  val state = stateInstance.component1()

  suspend fun loadItem(itemId: Uuid): T {
    val result =
      window
        .fetch("http://localhost:8082" + props.apiUri + "/${itemId.toString()}")
        .await()
        .text()
        .await()
    return props.decodeItem(result)
  }

  suspend fun updateItem(item: T) {
    val body = item.serialize()
    val headers = Headers()
    headers.append("Content-Type", "application/json")
    val fetchResult =
      window.fetch(
        "http://localhost:8082" + props.apiUri,
        RequestInit(method = "PATCH", headers = headers, body = body)
      )
    val response = fetchResult.await()
    response.text().await()
  }

  suspend fun insertItem(item: T) {
    val body = item.serialize()
    val headers = Headers()
    headers.append("Content-Type", "application/json")
    val fetchResult =
      window.fetch(
        "http://localhost:8082" + props.apiUri,
        RequestInit(method = "POST", headers = headers, body = body)
      )
    val response = fetchResult.await()
    val result = response.text().await()
    console.info(result)
  }

  fun switchToViewStateFunc(t: T) {
    GlobalScope.launch {
      val template = loadItem(t.id)
      updateState {
        jsObject {
          currentItem = template
          currentState = CrudState.VIEW
          itemsList = state.itemsList
        }
      }
    }
  }

  fun switchToAddStateFunc() {
    GlobalScope.launch {
      updateState {
        val currentState = it ?: jsObject<CrudComponentState<T>>()
        currentState.currentState = CrudState.ADD
        currentState.currentItem = null
        currentState
      }
    }
  }

  suspend fun switchToListViewStateFunc() {
    console.info("switchToListViewStateFunc")
    val result = window.fetch("http://localhost:8082" + props.apiUri).await().text().await()
    val templatesList = props.decodeItems(result)
    updateState {
      jsObject {
        currentItem = null
        currentState = CrudState.LIST
        itemsList = templatesList
      }
    }
  }

  fun switchToInsertStateFunc() {
    updateState {
      jsObject {
        currentItem = null
        currentState = CrudState.ADD
      }
    }
  }

  suspend fun deleteItem(t: T) {
    if (window.confirm("Are you sure you want to delete this item?")) {
      window
        .fetch(
          "http://localhost:8082" + props.apiUri + "/${t.id}",
          RequestInit(method = "DELETE")
        )
        .await()
      switchToListViewStateFunc()
    }
  }

  suspend fun submitEditItemForm(t: T) {
    console.info("inside submitEditTemplateForm")
    updateItem(t)
    switchToListViewStateFunc()
  }

  suspend fun submitInsertItemForm(t: T) {
    console.info("inside submitInsertTemplateForm")
    insertItem(t)
    switchToListViewStateFunc()
  }

  fun switchToEditStateFuncVar(t: T) {
    GlobalScope.launch {
      val template = loadItem(t.id)
      updateState {
        jsObject {
          currentState = CrudState.EDIT
          currentItem = template
        }
      }
    }
  }

  console.info("displaying component")
  if (state.currentState == null) {
    console.info("state is null")
    GlobalScope.launch { switchToListViewStateFunc() }
  }
  console.info("${state.currentState} updateState")
  when (state.currentState) {
    CrudState.LIST -> {
      TemplateList<T> {
        this.switchToEditState = ::switchToEditStateFuncVar
        this.switchToViewState = ::switchToViewStateFunc
        this.switchToInsertState = ::switchToInsertStateFunc
        this.deleteItem = ::deleteItem
        this.items = state.itemsList
      }
    }
    CrudState.EDIT -> TemplateEdit<T> {
      item = state.currentItem!!
      switchToListState = ::switchToListViewStateFunc
      submitForm = ::submitEditItemForm
    }
    CrudState.VIEW ->
      TemplateView<T> {
        item = state.currentItem!!
        switchToListState = ::switchToListViewStateFunc
      }
    CrudState.ADD -> TemplateInsert<T> {
      switchToListState = ::switchToListViewStateFunc
      submitForm = ::submitInsertItemForm
    }
  }
}

private val CrudComponent: FC<CrudComponentProps<IEditable<*>>> = fc { CrudComponent(it) }

fun <T : IEditable<T>> RBuilder.CrudComponent(block: CrudComponentProps<T>.() -> Unit) {
  child(type = CrudComponent, props = jsObject(block))
}