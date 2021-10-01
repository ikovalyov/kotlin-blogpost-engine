package com.github.ikovalyov.react.components.template

import com.benasher44.uuid.Uuid
import com.github.ikovalyov.model.markers.IEditable
import kotlinext.js.jsObject
import kotlinx.browser.window
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import org.w3c.fetch.Headers
import org.w3c.fetch.RequestInit
import react.FC
import react.PropsWithChildren
import react.RBuilder
import react.State
import react.fc
import react.useState

external interface CrudComponentProps<T> : PropsWithChildren {
    var decodeItem: (String) -> T
    var decodeItems: (String) -> List<T>
    var apiUri: String
    var factory: () -> T
}

external interface CrudComponentState<T> : State {
    var currentState: CrudState?
    var currentItem: T?
    var itemsList: List<T>?
}

enum class CrudState {
    LIST,
    VIEW,
    EDIT,
    ADD
}

@DelicateCoroutinesApi
private fun <T : IEditable<T>> RBuilder.CrudComponent(props: CrudComponentProps<T>) {
    val stateInstance = useState<CrudComponentState<T>> {
        jsObject()
    }
    val updateState = stateInstance.component2()
    val state = stateInstance.component1()

    suspend fun loadItem(itemId: Uuid): T {
        val result =
            window
                .fetch("http://localhost:8082" + props.apiUri + "/$itemId")
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
            window.fetch(
                "http://localhost:8082" + props.apiUri + "/${t.id}",
                RequestInit(method = "DELETE")
            )
                .await()
            switchToListViewStateFunc()
        }
    }

    suspend fun submitEditItemForm(t: T) {
        updateItem(t)
        switchToListViewStateFunc()
    }

    suspend fun submitInsertItemForm(t: T) {
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

    if (state.currentState == null) {
        GlobalScope.launch { switchToListViewStateFunc() }
    }
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
            item = props.factory()
        }
    }
}

private val CrudComponent: FC<CrudComponentProps<IEditable<*>>> = fc { CrudComponent(it) }

fun <T : IEditable<T>> RBuilder.CrudComponent(block: CrudComponentProps<T>.() -> Unit) {
    child(type = CrudComponent, props = jsObject(block))
}
