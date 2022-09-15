package com.github.ikovalyov.react.components.template

import com.github.ikovalyov.model.markers.IEditable
import csstype.Color
import csstype.Content
import csstype.Display
import csstype.Float
import csstype.FontWeight
import csstype.px
import emotion.react.css
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.js.jso
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

external interface TemplateInsertProps<T> : PropsWithChildren {
    var switchToListState: suspend () -> Unit
    var submitForm: suspend (t: T) -> Unit
    var item: T
}

external interface TemplateInsertState<T> : State {
    var currentItem: T?
}

class TemplateInsert<T : IEditable<T>>(
    props: TemplateInsertProps<T>,
    state: TemplateInsertState<T>
) : Component<TemplateInsertProps<T>, TemplateInsertState<T>>(props), CoroutineScope {

    init {
        setState(state)
    }

    private var job = Job()
    override val coroutineContext: CoroutineContext
        get() = job

    override fun render(): ReactNode {
        return Fragment.create {
            val currentItem = state.currentItem
            if (currentItem != null) {
                form {
                    onSubmit = {
                        it.preventDefault()
                        launch {
                            props.submitForm(currentItem)
                        }
                    }
                    fieldset {
                        val metadata = currentItem.getMetadata()

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
                                            content = csstype.Content.contents
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
                                            val value = event.target.asDynamic().value.toString()
                                            setState(
                                                { state ->
                                                    jso {
                                                        this.currentItem = state.currentItem!!.updateField(
                                                            field = it,
                                                            serializedData = value
                                                        )
                                                    }
                                                }
                                            )
                                        }
                                    } else {
                                        value = currentItem.getFieldValueAsString(it)
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
}
