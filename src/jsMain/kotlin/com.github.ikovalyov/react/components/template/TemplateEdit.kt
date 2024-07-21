package com.github.ikovalyov.react.components.template

import com.github.ikovalyov.model.markers.IEditable
import com.github.ikovalyov.model.markers.getFieldValueAsString
import com.github.ikovalyov.model.markers.getPredefinedValuesAsStrings
import com.github.ikovalyov.model.markers.updateField
import com.github.ikovalyov.react.components.template.table.buttonChild
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
import react.dom.html.ReactHTML.fieldset
import react.dom.html.ReactHTML.form
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.label
import react.dom.html.ReactHTML.option
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

external interface ItemEditProps<T> : PropsWithChildren {
    var switchToListState: suspend () -> Unit
    var submitForm: suspend (t: T) -> Unit
}

external interface TemplateEditState<T> : State {
    var item: T
}

class TemplateEdit<I : IEditable>(props: ItemEditProps<I>, private val initialState: TemplateEditState<I>) :
    Component<ItemEditProps<I>, TemplateEditState<I>>(props),
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

    override fun render(): ReactNode = Fragment.create {
        form {
            onSubmit =
                {
                    it.preventDefault()
                    launch {
                        props.submitForm(state.item)
                    }
                }
            fieldset {
                val fields = state.item.getMetadata().filterIsInstance<IEditable.EditableMetadata<*, I>>()
                fields.let {
                    it.forEach {
                        p {
                            label {
                                +it.fieldName
                                css {
                                    color = Color("B4886B")
                                    fontWeight = FontWeight.bold
                                    display = Display.block
                                    width = 150.px
                                    float = Float.left
                                    after { content = Content(":") }
                                }
                                htmlFor = it.hashCode().toString()
                            }
                            if (it.predefinedList.isNullOrEmpty()) {
                                input {
                                    name = it.hashCode().toString()
                                    readOnly = it.readOnly
                                    if (!it.readOnly) {
                                        defaultValue = getFieldValueAsString(it)
                                        onChange =
                                            { event ->
                                                launch {
                                                    val stringValue = event.target.asDynamic().value.toString()
                                                    state.item =
                                                        state.item.updateField(
                                                            field = it,
                                                            serializedData = stringValue,
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
                                                val stringValue = event.target.asDynamic().value.toString()
                                                state.item =
                                                    state.item.updateField(field = it, serializedData = stringValue)
                                            }
                                        val optionsList = getPredefinedValuesAsStrings(it)
                                        optionsList.forEach {
                                            option {
                                                value = it.key
                                                +it.value
                                            }
                                        }
                                        if (optionsList.isNotEmpty() && storedObject == null) {
                                            state.item = state.item.updateField(field = it, serializedData = optionsList.entries.first().value)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            buttonChild<I> {
                body = state.item
                text = "Update"
                type = ButtonType.submit
            }
            buttonChild<I> {
                onClick = {
                    launch { props.switchToListState() }
                }
                body = state.item
                text = "Cancel"
            }
        }
    }
}
