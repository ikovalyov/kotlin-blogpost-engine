package com.github.ikovalyov.react.components.template

import com.github.ikovalyov.model.markers.IEditable
import com.github.ikovalyov.react.components.template.table.Table
import kotlinext.js.jsObject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.html.ButtonType
import kotlinx.html.js.onClickFunction
import react.FC
import react.PropsWithChildren
import react.RBuilder
import react.dom.attrs
import react.dom.button
import react.fc

external interface TemplateListProps<T : Any> : PropsWithChildren {
  var switchToViewState: (T) -> Unit
  var switchToEditState: (T) -> Unit
  var switchToInsertState: () -> Unit
  var deleteItem: suspend (T) -> Unit
  var items: List<T>?
}

private fun <T : IEditable<T>> RBuilder.TemplateList(props: TemplateListProps<T>) {
  Table<T> {
    items = props.items?.toTypedArray()
    onViewClick = { props.switchToViewState(it) }
    onEditClick = { props.switchToEditState(it) }
    onDeleteClick = { GlobalScope.async { props.deleteItem(it) } }
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

private val TemplateList: FC<TemplateListProps<IEditable<*>>> = fc { TemplateList(it) }

fun <T : IEditable<T>> RBuilder.TemplateList(block: TemplateListProps<T>.() -> Unit) {
  child(type = TemplateList, props = jsObject(block))
}
