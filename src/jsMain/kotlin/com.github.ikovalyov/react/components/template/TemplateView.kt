package com.github.ikovalyov.react.components.template

import com.github.ikovalyov.model.Template
import com.github.ikovalyov.model.markers.IEditable
import com.github.ikovalyov.react.components.template.table.Button
import kotlinext.js.jsObject
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.html.unsafe
import react.FC
import react.PropsWithChildren
import react.RBuilder
import react.RComponent
import react.State
import react.dom.attrs
import react.dom.div
import react.dom.h1
import react.dom.iframe
import react.dom.p
import react.dom.section
import react.fc

external interface TemplateViewProps<T> : PropsWithChildren {
  var item: T
  var switchToListState: suspend () -> Unit
}

@DelicateCoroutinesApi
private fun <T: IEditable<T>> RBuilder.TemplateView(props: TemplateViewProps<T>) {
  div {
    val fields = props.item.getMetadata()
    fields.forEach {
      section {
        h1 { +it.fieldName }
        p { +props.item.getFieldValueAsString(it) }
      }
    }
    Button<T> {
      onClick = { GlobalScope.launch { props.switchToListState() } }
      body = props.item
      text = "Back to list"
    }
  }
}

@DelicateCoroutinesApi
private val TemplateView: FC<TemplateViewProps<IEditable<*>>> = fc { TemplateView(it) }

@DelicateCoroutinesApi
fun <T : IEditable<T>> RBuilder.TemplateView(block: TemplateViewProps<T>.() -> Unit) {
  child(type = TemplateView, props = jsObject(block))
}
