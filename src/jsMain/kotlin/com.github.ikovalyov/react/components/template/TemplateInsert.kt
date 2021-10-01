package com.github.ikovalyov.react.components.template

import com.github.ikovalyov.model.markers.IEditable
import kotlinext.js.jsObject
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.css.Color
import kotlinx.css.Display
import kotlinx.css.Float
import kotlinx.css.FontWeight
import kotlinx.css.LinearDimension
import kotlinx.css.QuotedString
import kotlinx.css.color
import kotlinx.css.content
import kotlinx.css.display
import kotlinx.css.float
import kotlinx.css.fontWeight
import kotlinx.css.width
import kotlinx.html.ButtonType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onSubmitFunction
import react.FC
import react.PropsWithChildren
import react.RBuilder
import react.State
import react.dom.attrs
import react.dom.button
import react.dom.defaultValue
import react.dom.fieldset
import react.dom.form
import react.dom.input
import react.fc
import react.useState
import styled.css
import styled.styledLabel
import styled.styledP

external interface TemplateInsertProps<T> : PropsWithChildren {
    var switchToListState: suspend () -> Unit
    var submitForm: suspend (t: T) -> Unit
    var item: T
}

external interface TemplateInsertState<T> : State {
    var currentItem: T?
}

@DelicateCoroutinesApi
private fun <T : IEditable<T>> RBuilder.TemplateInsert(props: TemplateInsertProps<T>) {
    val stateInstance = useState<TemplateInsertState<T>> {
        jsObject {
            currentItem = props.item
        }
    }
    val state = stateInstance.component1()
    val updateState = stateInstance.component2()
    val currentItem = state.currentItem
    if (currentItem != null) {
        form {
            attrs {
                onSubmitFunction =
                    {
                        it.preventDefault()
                        GlobalScope.launch {
                            props.submitForm(state.currentItem!!)
                        }
                    }
            }
            fieldset {
                val metadata = currentItem.getMetadata()
                metadata.forEach {
                    styledP {
                        styledLabel {
                            +it.fieldName
                            css {
                                color = Color("B4886B")
                                fontWeight = FontWeight.bold
                                display = Display.block
                                width = LinearDimension("150px")
                                float = Float.left
                                after { content = QuotedString(":") }
                            }
                            attrs { this.attributes["htmlFor"] = it.fieldName }
                        }
                        input {
                            attrs {
                                name = it.fieldName
                                readonly = it.readOnly
                                if (!it.readOnly) {
                                    defaultValue = ""
                                    onChangeFunction = { event ->
                                        val value = event.target.asDynamic().value.toString()
                                        updateState { state ->
                                            jsObject {
                                                this.currentItem = state.currentItem?.updateField(field = it, serializedData = value)
                                            }
                                        }
                                    }
                                } else {
                                    value = currentItem.getFieldValueAsString(it)
                                }
                            }
                        }
                    }
                }
                button {
                    attrs {
                        text("Insert")
                        name = "insert"
                        type = ButtonType.submit
                    }
                }
                button {
                    attrs {
                        text("Cancel")
                        name = "insert"
                        type = ButtonType.button
                        onClickFunction = { GlobalScope.launch { props.switchToListState() } }
                    }
                }
            }
        }
    }
}

@DelicateCoroutinesApi
private val TemplateInsert: FC<TemplateInsertProps<IEditable<*>>> = fc { TemplateInsert(it) }

@DelicateCoroutinesApi
fun <T : IEditable<T>> RBuilder.TemplateInsert(block: TemplateInsertProps<T>.() -> Unit) {
    child(type = TemplateInsert, props = jsObject(block))
}
