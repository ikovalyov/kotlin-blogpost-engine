package com.github.ikovalyov.react.components.template

import com.benasher44.uuid.Uuid
import com.github.ikovalyov.coroutines.SimpleCoroutineScope
import com.github.ikovalyov.model.markers.IEditable
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import kotlinx.js.jso
import org.w3c.fetch.Headers
import org.w3c.fetch.RequestInit
import react.Component
import react.Fragment
import react.PropsWithChildren
import react.ReactNode
import react.State
import react.create
import react.useState
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.asPromise
import kotlinx.coroutines.async
import react.ChildrenBuilder
import react.FC
import react.useEffect

external interface CrudComponentProps<T> : PropsWithChildren {
    var decodeItem: (String) -> T
    var decodeItems: (String) -> List<T>
    var apiUri: String
    var factory: () -> T
    var initialState: CrudState
}

external interface CrudComponentState<T> : State {
    var currentState: CrudState
    var currentItem: T?
    var itemsList: List<T>?
}

enum class CrudState {
    LIST,
    VIEW,
    EDIT,
    ADD,
    INIT
}

private val coroutineScope = SimpleCoroutineScope()

private fun <I : IEditable> ChildrenBuilder.CrudComponent(props: CrudComponentProps<I>) {

    val stateInstance = useState<CrudComponentState<I>>{
        jso {
            currentState = CrudState.INIT
        }
    }
    val updateState = stateInstance.component2()
    val componentState = stateInstance.component1()

    suspend fun loadItem(itemId: Uuid): I {
        val result = window
            .fetch("http://localhost:8082" + props.apiUri + "/$itemId")
            .await()
            .text()
            .await()
        return props.decodeItem(result)
    }

    suspend fun updateItem(item: I) {
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

    suspend fun insertItem(item: I) {
        val body = item.serialize()
        val headers = Headers()
        headers.append("Content-Type", "application/json")
        val fetchResult =
            window.fetch(
                "http://localhost:8082" + props.apiUri,
                RequestInit(method = "POST", headers = headers, body = body)
            )
        val response = fetchResult.await()
        response.text().await()
    }

    fun switchToViewStateFunc(t: I) {
        coroutineScope.launch {
            val template = loadItem(t.id)
            updateState {
                it.currentItem = template
                it.currentState = CrudState.VIEW
                it.itemsList = componentState.itemsList
                it
            }
        }
    }

    fun switchToAddStateFunc() {
        coroutineScope.launch {
            updateState {
                it.currentState = CrudState.ADD
                it.currentItem = null
                it
            }
        }
    }

    suspend fun switchToListViewStateFunc() {
        coroutineScope.launch {
            val result = window.fetch("http://localhost:8082" + props.apiUri).await().text().await()
            val templatesList = props.decodeItems(result)
            updateState {
                jso {
                    currentItem = null
                    currentState = CrudState.LIST
                    itemsList = templatesList
                }
            }
        }
    }

    fun switchToInsertStateFunc() {
        updateState {
            it.currentItem = null
            it.currentState = CrudState.ADD
            it
        }
    }

    suspend fun deleteItem(t: I) {
        if (window.confirm("Are you sure you want to delete this item?")) {
            window.fetch(
                "http://localhost:8082" + props.apiUri + "/${t.id}",
                RequestInit(method = "DELETE")
            )
                .await()
            switchToListViewStateFunc()
        }
    }

    suspend fun submitEditItemForm(t: I) {
        updateItem(t)
        switchToListViewStateFunc()
    }

    suspend fun submitInsertItemForm(t: I) {
        insertItem(t)
        switchToListViewStateFunc()
    }

    fun switchToEditStateFuncVar(t: I) {
        coroutineScope.launch {
            val template = loadItem(t.id)
            updateState {
                it.currentItem = template
                it.currentState = CrudState.EDIT
                it
            }
        }
    }

    console.log("Displaying component, current state is ${componentState.currentState.name}")

    when (componentState.currentState) {
        CrudState.INIT -> {
            console.log("switching to list view")
            coroutineScope.launch {
                switchToListViewStateFunc()
            }
        }

        CrudState.LIST -> {
            console.log("Loading ItemList")
            child(ItemList(jso<ItemListProps<I>> {
                switchToEditState = ::switchToEditStateFuncVar
                switchToViewState = ::switchToViewStateFunc
                switchToInsertState = ::switchToInsertStateFunc
                deleteItem = ::deleteItem
                items = componentState.itemsList
            }).render())
        }

        CrudState.EDIT -> {
            console.log("Loading TemplateEdit")
            TemplateEdit(
                jso {
                    switchToListState = ::switchToListViewStateFunc
                    submitForm = ::submitEditItemForm
                },
                jso<TemplateEditState<I>> {
                    item = componentState.currentItem!!
                }
            )
        }

        CrudState.VIEW -> {
            console.log("Loading TemplateView")
            TemplateView(
                jso<TemplateViewProps<I>> {
                    switchToListState = ::switchToListViewStateFunc
                },
                jso {
                    item = componentState.currentItem!!
                }
            )
        }

        CrudState.ADD -> {
            console.log("Loading TemplateInsert")
            TemplateInsert(
                jso<TemplateInsertProps<I>> {
                    this.item = props.factory()
                    this.submitForm = ::submitInsertItemForm
                    this.switchToListState = ::switchToListViewStateFunc
                },
                jso {
                    this.currentItem = props.factory()
                }
            )
        }
    }
}

private val CrudComponent = FC<CrudComponentProps<IEditable>> {
    CrudComponent(it)
}

fun <T : IEditable> ChildrenBuilder.CrudComponent(block: CrudComponentProps<T>.() -> Unit) {
    child(type = CrudComponent, props = jso(block))
}