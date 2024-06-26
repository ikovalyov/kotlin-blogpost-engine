package com.github.ikovalyov.react.components.template

import com.benasher44.uuid.Uuid
import com.github.ikovalyov.Api.backendEndpoint
import com.github.ikovalyov.coroutines.SimpleCoroutineScope
import com.github.ikovalyov.model.markers.IEditable
import js.objects.jso
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import org.w3c.fetch.Headers
import org.w3c.fetch.INCLUDE
import org.w3c.fetch.RequestCredentials
import org.w3c.fetch.RequestInit
import react.ChildrenBuilder
import react.FC
import react.PropsWithChildren
import react.State
import react.dom.html.ReactHTML.h2
import react.useState

external interface CrudComponentProps<T> : PropsWithChildren {
    var decodeItem: (String) -> T
    var decodeItems: (String) -> List<T>
    var apiUri: String
    var factory: () -> T
    var header: String
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
    val stateInstance = useState<CrudComponentState<I>> {
        jso {
            currentState = CrudState.INIT
        }
    }
    val updateState = stateInstance.component2()
    val componentState = stateInstance.component1()

    suspend fun loadItem(itemId: Uuid): I {
        val result = window
            .fetch(
                backendEndpoint + props.apiUri + "/$itemId",
                RequestInit(
                    credentials = RequestCredentials.INCLUDE
                )
            )
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
                backendEndpoint + props.apiUri,
                RequestInit(
                    method = "PATCH",
                    headers = headers,
                    body = body,
                    credentials = RequestCredentials.INCLUDE
                )
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
                backendEndpoint + props.apiUri,
                RequestInit(
                    method = "POST",
                    headers = headers,
                    body = body,
                    credentials = RequestCredentials.INCLUDE
                )
            )
        val response = fetchResult.await()
        response.text().await()
    }

    fun switchToViewStateFunc(t: I) {
        coroutineScope.launch {
            val template = loadItem(t.id)
            updateState {
                jso {
                    currentItem = template
                    currentState = CrudState.VIEW
                    itemsList = componentState.itemsList
                }
            }
        }
    }

    suspend fun switchToListViewStateFunc() {
        coroutineScope.launch {
            val result = window.fetch(
                backendEndpoint + props.apiUri,
                RequestInit(
                    credentials = RequestCredentials.INCLUDE
                )
            ).await().text().await()
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
            jso {
                currentItem = null
                currentState = CrudState.ADD
                itemsList = componentState.itemsList
            }
        }
    }

    suspend fun deleteItem(t: I) {
        if (window.confirm("Are you sure you want to delete this item?")) {
            val headers = Headers()
            headers.append("Content-Type", "application/json")
            window.fetch(
                backendEndpoint + props.apiUri + "/${t.id}",
                RequestInit(
                    method = "DELETE",
                    credentials = RequestCredentials.INCLUDE
                )
            ).await()
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
                jso {
                    currentItem = template
                    currentState = CrudState.EDIT
                    itemsList = it.itemsList
                }
            }
        }
    }

    console.log("Displaying component, current state is ${componentState.currentState.name}")
    h2 {
        +props.header
    }

    when (componentState.currentState) {
        CrudState.INIT -> {
            console.log("switching to list view")
            coroutineScope.launch {
                switchToListViewStateFunc()
            }
        }

        CrudState.LIST -> {
            console.log("Loading ItemList")
            +ItemList(
                jso<ItemListProps<I>> {
                    switchToEditState = ::switchToEditStateFuncVar
                    switchToViewState = ::switchToViewStateFunc
                    switchToInsertState = ::switchToInsertStateFunc
                    deleteItem = ::deleteItem
                    items = componentState.itemsList
                }
            ).render()
        }

        CrudState.EDIT -> {
            console.log("Loading TemplateEdit")
            +TemplateEdit(
                jso {
                    switchToListState = ::switchToListViewStateFunc
                    submitForm = ::submitEditItemForm
                },
                jso<TemplateEditState<I>> {
                    item = componentState.currentItem!!
                }
            ).render()
        }

        CrudState.VIEW -> {
            console.log("Loading TemplateView")
            +TemplateView(
                jso<TemplateViewProps<I>> {
                    switchToListState = ::switchToListViewStateFunc
                },
                jso {
                    item = componentState.currentItem!!
                }
            ).render()
        }

        CrudState.ADD -> {
            console.log("Loading TemplateInsert")
            +TemplateInsert(
                jso<TemplateInsertProps<I>> {
                    this.item = props.factory()
                    this.submitForm = ::submitInsertItemForm
                    this.switchToListState = ::switchToListViewStateFunc
                },
                jso {
                    this.currentItem = props.factory()
                }
            ).render()
        }
    }
}

private val CrudComponent = FC<CrudComponentProps<IEditable>> {
    CrudComponent(it)
}

fun <T : IEditable> ChildrenBuilder.CrudComponent(block: CrudComponentProps<T>.() -> Unit) {
    child(type = CrudComponent, props = jso(block))
}
