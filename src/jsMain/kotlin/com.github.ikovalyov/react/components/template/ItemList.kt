package com.github.ikovalyov.react.components.template

import com.github.ikovalyov.model.markers.IEditable
import com.github.ikovalyov.react.components.template.table.tableChild
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import react.Component
import react.Fragment
import react.PropsWithChildren
import react.ReactNode
import react.create
import react.dom.html.ReactHTML
import web.html.ButtonType
import kotlin.coroutines.CoroutineContext

external interface ItemListProps<T : Any> : PropsWithChildren {
    var switchToViewState: (T) -> Unit
    var switchToEditState: (T) -> Unit
    var switchToInsertState: () -> Unit
    var deleteItem: suspend (T) -> Unit
    var items: List<T>?
}

class ItemList<I : IEditable>(props: ItemListProps<I>) :
    Component<ItemListProps<I>, CrudComponentState<I>>(props),
    CoroutineScope {

    private var job = Job()
    override val coroutineContext: CoroutineContext
        get() = job

    override fun render(): ReactNode = Fragment.create {
        tableChild<I> {
            items = props.items?.toTypedArray()
            onViewClick = {
                console.log("switch to view state")
                props.switchToViewState(it)
            }
            onEditClick = {
                console.log("switch to edit state")
                props.switchToEditState(it)
            }
            onDeleteClick = {
                console.log("delete")
                launch { props.deleteItem(it) }
            }
        }

        ReactHTML.button {
            +"Add new"
            name = "new"
            type = ButtonType.button
            onClick = { props.switchToInsertState() }
        }
    }
}
