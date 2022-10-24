package com.github.ikovalyov.react.components.template

import com.github.ikovalyov.model.markers.IEditable
import com.github.ikovalyov.model.markers.getFieldValueAsString
import com.github.ikovalyov.react.components.template.table.Button
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import react.Component
import react.Fragment
import react.PropsWithChildren
import react.ReactNode
import react.State
import react.create
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.p
import react.dom.html.ReactHTML.section
import kotlin.coroutines.CoroutineContext

external interface TemplateViewProps<I> : PropsWithChildren {
    var switchToListState: suspend () -> Unit
}

external interface TemplateViewState<I> : State {
    var item: I
}

class TemplateView<I : IEditable>(
    props: TemplateViewProps<I>,
    state: TemplateViewState<I>
) : Component<TemplateViewProps<I>, TemplateViewState<I>>(props), CoroutineScope {

    init {
        this.state = state
    }

    private var job = Job()
    override val coroutineContext: CoroutineContext
        get() = job

    override fun render(): ReactNode {
        return Fragment.create {
            div {
                val fields = state.item.getMetadata().filterIsInstance<IEditable.EditableMetadata<*, I>>()
                fields.forEach {
                    section {
                        h1 { +it.fieldType::class.simpleName!! }
                        p { +state.item.getFieldValueAsString(it) }
                    }
                }
                Button<I> {
                    onClick = {
                        launch { props.switchToListState() }
                    }
                    body = state.item
                    text = "Back to list"
                }
            }
        }
    }
}
