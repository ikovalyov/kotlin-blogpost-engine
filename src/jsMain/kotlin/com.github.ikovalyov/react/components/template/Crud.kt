package com.github.ikovalyov.react.components.template

import com.benasher44.uuid.Uuid
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

external interface CrudComponentProps<T> : PropsWithChildren {
    var decodeItem: (String) -> T
    var decodeItems: (String) -> List<T>
    var apiUri: String
    var factory: () -> T
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

class CrudComponent<T : IEditable<T>>(props: CrudComponentProps<T>) :
    Component<CrudComponentProps<T>, CrudComponentState<T>>(props),
    CoroutineScope {

    private var job = Job()
    override val coroutineContext: CoroutineContext
        get() = job

    private val stateInstance = useState<CrudComponentState<T>> {
        jso {
            currentState = CrudState.INIT
        }
    }
    val updateState = stateInstance.component2()
    private val componentState = stateInstance.component1()

    private suspend fun loadItem(itemId: Uuid): T {
        val result = window
            .fetch("http://localhost:8082" + props.apiUri + "/$itemId")
            .await()
            .text()
            .await()
        return props.decodeItem(result)
    }

    private suspend fun updateItem(item: T) {
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

    private suspend fun insertItem(item: T) {
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

    fun switchToViewStateFunc(t: T) {
        launch {
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
        launch {
            updateState {
                it.currentState = CrudState.ADD
                it.currentItem = null
                it
            }
        }
    }

    private suspend fun switchToListViewStateFunc() {
        val result = window.fetch("http://localhost:8082" + props.apiUri).await().text().await()
        val templatesList = props.decodeItems(result)
        updateState {
            it.currentItem = null
            it.currentState = CrudState.LIST
            it.itemsList = templatesList
            it
        }
    }

    private fun switchToInsertStateFunc() {
        updateState {
            it.currentItem = null
            it.currentState = CrudState.ADD
            it
        }
    }

    private suspend fun deleteItem(t: T) {
        if (window.confirm("Are you sure you want to delete this item?")) {
            window.fetch(
                "http://localhost:8082" + props.apiUri + "/${t.id}",
                RequestInit(method = "DELETE")
            )
                .await()
            switchToListViewStateFunc()
        }
    }

    private suspend fun submitEditItemForm(t: T) {
        updateItem(t)
        switchToListViewStateFunc()
    }

    private suspend fun submitInsertItemForm(t: T) {
        insertItem(t)
        switchToListViewStateFunc()
    }

    private fun switchToEditStateFuncVar(t: T) {
        launch {
            val template = loadItem(t.id)
            updateState {
                it.currentItem = template
                it.currentState = CrudState.EDIT
                it
            }
        }
    }

    override fun render(): ReactNode {
        return Fragment.create {
            when (componentState.currentState) {
                CrudState.INIT -> {
                    launch { switchToListViewStateFunc() }
                }
                CrudState.LIST -> {
                    val props: ItemListProps<T> = jso {
                        switchToEditState = ::switchToEditStateFuncVar
                        switchToViewState = ::switchToViewStateFunc
                        switchToInsertState = ::switchToInsertStateFunc
                        deleteItem = ::deleteItem
                        items = componentState.itemsList
                    }
                    ItemList(props)
                }
                CrudState.EDIT -> TemplateEdit(
                    jso {
                        switchToListState = ::switchToListViewStateFunc
                        submitForm = ::submitEditItemForm
                    },
                    jso<TemplateEditState<T>> {
                        item = componentState.currentItem!!
                    }
                )
                CrudState.VIEW ->
                    TemplateView(
                        jso<TemplateViewProps<T>> {
                            switchToListState = ::switchToListViewStateFunc
                        },
                        jso {
                            item = componentState.currentItem!!
                        }
                    )
                CrudState.ADD -> TemplateInsert(
                    jso<TemplateInsertProps<T>> {
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
}
