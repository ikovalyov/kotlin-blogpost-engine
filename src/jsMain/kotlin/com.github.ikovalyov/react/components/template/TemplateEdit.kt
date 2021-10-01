package com.github.ikovalyov.react.components.template

import com.github.ikovalyov.model.markers.IEditable
import com.github.ikovalyov.react.components.template.table.Button
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
import kotlinx.html.js.onSubmitFunction
import react.FC
import react.PropsWithChildren
import react.RBuilder
import react.State
import react.dom.attrs
import react.dom.defaultValue
import react.dom.fieldset
import react.dom.form
import react.dom.input
import react.fc
import react.useState
import styled.css
import styled.styledLabel
import styled.styledP

external interface ItemEditProps<T> : PropsWithChildren {
    var item: T
    var switchToListState: suspend () -> Unit
    var submitForm: suspend (t: T) -> Unit
}

external interface TemplateEditState<T> : State {
    var item: T
}

@DelicateCoroutinesApi
private fun <T : IEditable<T>> RBuilder.TemplateEdit(props: ItemEditProps<T>) {
    val stateInstance = useState<TemplateEditState<T>> {
        jsObject {
            item = props.item
        }
    }
    val updateState = stateInstance.component2()
    val state = stateInstance.component1()
    form {
        attrs {
            onSubmitFunction =
                {
                    it.preventDefault()
                    GlobalScope.launch {
                        props.submitForm(state.item)
                    }
                }
        }
        fieldset {
            val fields = state.item.getMetadata()
            fields.let {
                it.forEach {
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
                            attrs { this.attributes["htmlFor"] = "id" }
                        }
                        input {
                            attrs {
                                name = it.fieldName
                                readonly = it.readOnly
                                if (!it.readOnly) {
                                    defaultValue = state.item.getFieldValueAsString(it)
                                    onChangeFunction =
                                        { event ->
                                            val stringValue = event.target.asDynamic().value.toString()
                                            updateState { state ->
                                                jsObject {
                                                    item = state.item.updateField(field = it, serializedData = stringValue)
                                                }
                                            }
                                        }
                                } else {
                                    value = state.item.getFieldValue(it).toString()
                                }
                            }
                        }
                    }
                }
            }
        }
        Button<T> {
            body = state.item
            text = "Update"
            type = ButtonType.submit
        }
        Button<T> {
            onClick = { GlobalScope.launch { props.switchToListState() } }
            body = state.item
            text = "Cancel"
        }
    }
}

private val TemplateEdit: FC<ItemEditProps<IEditable<*>>> = fc { TemplateEdit(it) }

fun <T : IEditable<T>> RBuilder.TemplateEdit(block: ItemEditProps<T>.() -> Unit) {
    child(type = TemplateEdit, props = jsObject(block))
}
