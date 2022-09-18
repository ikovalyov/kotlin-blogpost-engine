package com.github.ikovalyov.react.components.template

import com.github.ikovalyov.model.markers.IEditable
import com.github.ikovalyov.model.markers.getFieldValueAsString
import com.github.ikovalyov.model.markers.updateField
import com.github.ikovalyov.react.components.template.table.Button
import csstype.Color
import csstype.Display
import csstype.Float
import csstype.FontWeight
import csstype.px
import emotion.react.css
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import react.Component
import react.Fragment
import react.PropsWithChildren
import react.ReactNode
import react.State
import react.create
import react.dom.html.ButtonType
import react.dom.html.ReactHTML.fieldset
import react.dom.html.ReactHTML.form
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.label
import react.dom.html.ReactHTML.p
import kotlin.coroutines.CoroutineContext

external interface ItemEditProps<T> : PropsWithChildren {
    var switchToListState: suspend () -> Unit
    var submitForm: suspend (t: T) -> Unit
}

external interface TemplateEditState<T> : State {
    var item: T
}

class TemplateEdit<I : IEditable>(
    props: ItemEditProps<I>,
    private val initialState: TemplateEditState<I>
) : Component<ItemEditProps<I>, TemplateEditState<I>>(props),
    CoroutineScope {

    init {
        this.state = initialState
    }

    private var job = Job()
    override val coroutineContext: CoroutineContext
        get() = job

    override fun componentDidMount() {
        super.componentDidMount()
        setState(initialState)
    }

    override fun render(): ReactNode {
        return Fragment.create {
            form {
                onSubmit =
                    {
                        it.preventDefault()
                        launch {
                            props.submitForm(state.item)
                        }
                    }
            }
            fieldset {
                val fields = state.item.getMetadata().filterIsInstance<IEditable.EditableMetadata<*, I>>()
                fields.let {
                    it.forEach {
                        p {
                            label {
                                +it.fieldType::class.simpleName!!
                                css {
                                    color = Color("B4886B")
                                    fontWeight = FontWeight.bold
                                    display = Display.block
                                    width = 150.px
                                    float = Float.left
                                    after { content = "\":\"".asDynamic() }
                                }
                                htmlFor = "id"
                            }
                            input {
                                name = it.hashCode().toString()
                                readOnly = it.readOnly
                                if (!it.readOnly) {
                                    defaultValue = state.item.getFieldValueAsString(it)
                                    onChange =
                                        { event ->
                                            val stringValue = event.target.asDynamic().value.toString()
                                            state.item = state.item.updateField(field = it, serializedData = stringValue)
                                        }
                                } else {
                                    value = state.item.getFieldValueAsString(it)
                                }
                            }
                        }
                    }
                }
            }
            Button<I> {
                body = state.item
                text = "Update"
                type = ButtonType.submit
            }
            Button<I> {
                onClick = {
                    launch { props.switchToListState() }
                }
                body = state.item
                text = "Cancel"
            }
        }
    }
}
