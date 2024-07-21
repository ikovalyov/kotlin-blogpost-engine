package com.github.ikovalyov.react.components.template

import com.github.ikovalyov.model.markers.IEditable
import com.github.ikovalyov.model.markers.getFieldValueAsString
import com.github.ikovalyov.model.markers.getPredefinedValuesAsStrings
import com.github.ikovalyov.model.markers.updateField
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
import react.dom.html.ReactHTML
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.fieldset
import react.dom.html.ReactHTML.form
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.label
import react.dom.html.ReactHTML.p
import react.dom.html.ReactHTML.select
import web.cssom.Color
import web.cssom.Content
import web.cssom.Display
import web.cssom.Float
import web.cssom.FontWeight
import web.cssom.px
import web.html.ButtonType
import kotlin.coroutines.CoroutineContext

external interface TemplateInsertProps<I : IEditable> : PropsWithChildren {
    var switchToListState: suspend () -> Unit
    var submitForm: suspend (t: I) -> Unit
    var item: I
}

external interface TemplateInsertState<I : IEditable> : State {
    var currentItem: I
}

class TemplateInsert<I : IEditable>(props: TemplateInsertProps<I>, initialState: TemplateInsertState<I>) :
    Component<TemplateInsertProps<I>, TemplateInsertState<I>>(props),
    CoroutineScope {

    init {
        state = initialState
    }

    private var job = Job()
    override val coroutineContext: CoroutineContext
        get() = job

    override fun render(): ReactNode = Fragment.create {
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
                            +it.fieldName
                            css {
                                color = Color("B4886B")
                                fontWeight = FontWeight.bold
                                display = Display.block
                                width = 150.px
                                float = Float.left
                                after {
                                    content = Content(":")
                                }
                            }
                            htmlFor = it.hashCode().toString()
                        }
                        if (it.predefinedList.isNullOrEmpty()) {
                            input {
                                name = it.hashCode().toString()
                                readOnly = it.readOnly
                                if (!it.readOnly) {
                                    defaultValue = ""
                                    onChange = { event ->
                                        launch {
                                            val value = event.target.value
                                            state.currentItem = state.currentItem.updateField(
                                                field = it,
                                                serializedData = value,
                                            )
                                        }
                                    }
                                } else {
                                    value = getFieldValueAsString(it)
                                }
                            }
                        } else {
                            select {
                                name = it.hashCode().toString()
                                disabled = it.readOnly
                                if (!it.readOnly) {
                                    val storedObject = it.get()
                                    if (storedObject != null) {
                                        defaultValue = getFieldValueAsString(it)
                                    }
                                    onChange =
                                        { event ->
                                            launch {
                                                val stringValue = event.target.options.item(event.target.selectedIndex)!!.text
                                                state.currentItem =
                                                    state.currentItem.updateField(
                                                        field = it,
                                                        serializedData = stringValue,
                                                    )
                                            }
                                        }
                                    val optionsList = getPredefinedValuesAsStrings(it)
                                    optionsList.forEach {
                                        ReactHTML.option {
                                            value = it.key
                                            +it.value
                                        }
                                    }
                                    if (optionsList.isNotEmpty() && storedObject == null) {
                                        val stringValue = optionsList.values.first()
                                        state.currentItem =
                                            state.currentItem.updateField(
                                                field = it,
                                                serializedData = stringValue,
                                            )
                                    }
                                }
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
