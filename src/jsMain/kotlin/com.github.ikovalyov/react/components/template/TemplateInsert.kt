package com.github.ikovalyov.react.components.template

import com.github.ikovalyov.model.markers.IEditable
import com.github.ikovalyov.model.markers.getFieldValueAsString
import com.github.ikovalyov.model.markers.updateField
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
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.fieldset
import react.dom.html.ReactHTML.form
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.label
import react.dom.html.ReactHTML.p
import kotlin.coroutines.CoroutineContext

external interface TemplateInsertProps<I : IEditable> : PropsWithChildren {
    var switchToListState: suspend () -> Unit
    var submitForm: suspend (t: I) -> Unit
    var item: I
}

external interface TemplateInsertState<I : IEditable> : State {
    var currentItem: I
}

class TemplateInsert<I : IEditable>(
    props: TemplateInsertProps<I>,
    private val initialState: TemplateInsertState<I>
) : Component<TemplateInsertProps<I>, TemplateInsertState<I>>(props), CoroutineScope {

    init {
        state = initialState
    }

    private var job = Job()
    override val coroutineContext: CoroutineContext
        get() = job

    override fun render(): ReactNode {
        val ref = this
        return Fragment.create {
            form {
                onSubmit = {
                    it.preventDefault()
                    launch {
                        props.submitForm(state.currentItem)
                    }
                }
                fieldset {
                    val metadata = state.currentItem.getMetadata().filterIsInstance<IEditable.EditableMetadata<*, I>>()

                    metadata.forEach {
                        p {
                            label {
                                +it.fieldType::class.simpleName!!
                                css {
                                    color = csstype.Color("B4886B")
                                    fontWeight = csstype.FontWeight.bold
                                    display = csstype.Display.block
                                    width = 150.px
                                    float = csstype.Float.left
                                    after {
                                        content = "\":\"".asDynamic()
                                    }
                                }
                                htmlFor = it.hashCode().toString()
                            }
                            input {
                                name = it.hashCode().toString()
                                readOnly = it.readOnly
                                if (!it.readOnly) {
                                    defaultValue = ""
                                    onChange = { event ->
                                        launch {
                                            val value = event.target.value
                                            ref.state.currentItem = ref.state.currentItem.updateField(
                                                field = it,
                                                serializedData = value
                                            )
                                        }
                                    }
                                } else {
                                    value = state.currentItem.getFieldValueAsString(it)
                                }
                            }
                        }
                    }

                    button {
                        name = "insert"
                        type = ButtonType.submit
                        +"Insert"
                    }

                    button {
                        name = "Cancel"
                        type = ButtonType.button
                        +"Cancel"
                        onClick = {
                            launch { props.switchToListState() }
                        }
                    }
                }
            }
        }
    }
}
