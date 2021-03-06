package com.github.ikovalyov.react.components.template

import com.github.ikovalyov.model.markers.IEditable
import com.github.ikovalyov.react.components.template.table.Button
import kotlinext.js.jsObject
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import react.FC
import react.PropsWithChildren
import react.RBuilder
import react.dom.div
import react.dom.h1
import react.dom.p
import react.dom.section
import react.fc

external interface TemplateViewProps<T> : PropsWithChildren {
    var item: T
    var switchToListState: suspend () -> Unit
}

@DelicateCoroutinesApi
private fun <T : IEditable<T>> RBuilder.TemplateView(props: TemplateViewProps<T>) {
    div {
        val fields = props.item.getMetadata()
        fields.forEach {
            section {
                h1 { +it.fieldType::class.simpleName!! }
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
@Suppress("TYPE_MISMATCH_WARNING")
private val TemplateView: FC<TemplateViewProps<IEditable<*>>> = fc { TemplateView(it) }

@DelicateCoroutinesApi
fun <T : IEditable<T>> RBuilder.TemplateView(block: TemplateViewProps<T>.() -> Unit) {
    child(type = TemplateView, props = jsObject(block))
}
