package com.github.ikovalyov.react.components.template

import com.github.ikovalyov.model.markers.IEditable
import com.github.ikovalyov.react.components.template.table.Table
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import react.Component
import react.Fragment
import react.PropsWithChildren
import react.ReactNode
import react.create
import react.dom.html.ButtonType
import react.dom.html.ReactHTML
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

    override fun render(): ReactNode? {
        return Fragment.create {
            Table<I> {
                items = props.items?.toTypedArray()
                onViewClick = { props.switchToViewState(it) }
                onEditClick = { props.switchToEditState(it) }
                onDeleteClick = { launch { props.deleteItem(it) } }
            }

            ReactHTML.button {
                +"Add new"
                name = "new"
                type = ButtonType.button
                onClick = { props.switchToInsertState() }
            }
        }
    }
}
