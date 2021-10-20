package com.github.ikovalyov.react.components.template

import com.github.ikovalyov.model.markers.IEditable
import com.github.ikovalyov.react.components.template.table.Table
import kotlinext.js.jsObject
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.html.ButtonType
import kotlinx.html.js.onClickFunction
import react.FC
import react.PropsWithChildren
import react.RBuilder
import react.dom.attrs
import react.dom.button
import react.fc

external interface ItemListProps<T : Any> : PropsWithChildren {
    var switchToViewState: (T) -> Unit
    var switchToEditState: (T) -> Unit
    var switchToInsertState: () -> Unit
    var deleteItem: suspend (T) -> Unit
    var items: List<T>?
}

@DelicateCoroutinesApi
private fun <T : IEditable<T>> RBuilder.ItemList(props: ItemListProps<T>) {
    Table<T> {
        items = props.items?.toTypedArray()
        onViewClick = { props.switchToViewState(it) }
        onEditClick = { props.switchToEditState(it) }
        onDeleteClick = { GlobalScope.launch { props.deleteItem(it) } }
    }

    button {
        attrs {
            text("Add new")
            name = "new"
            type = ButtonType.button
            onClickFunction = { props.switchToInsertState() }
        }
    }
}

@DelicateCoroutinesApi
@Suppress("TYPE_MISMATCH_WARNING")
private val ItemList: FC<ItemListProps<IEditable<*>>> = fc { ItemList(it) }

@DelicateCoroutinesApi
fun <T : IEditable<T>> RBuilder.ItemList(block: ItemListProps<T>.() -> Unit) {
    child(type = ItemList, props = jsObject(block))
}
