package com.github.ikovalyov.react.components.template.table

import com.github.ikovalyov.model.markers.IEditable
import kotlinext.js.jsObject
import kotlinx.html.ButtonType
import kotlinx.html.js.onClickFunction
import react.FC
import react.PropsWithChildren
import react.RBuilder
import react.dom.attrs
import react.dom.button
import react.fc

external interface ButtonProps<T : IEditable<T>> : PropsWithChildren {
    var body: T
    var onClick: ((T) -> Unit)?
    var text: String
    var type: ButtonType?
}

private fun <T : IEditable<T>> RBuilder.Button(props: ButtonProps<T>) {
    button {
        attrs {
            text(props.text)
            value = props.body.id.toString()
            name = "edit"
            type = props.type ?: ButtonType.button
            onClickFunction = { props.onClick?.invoke(props.body) }
        }
    }
}

private val Button: FC<ButtonProps<*>> = fc { Button(it) }

fun <T : IEditable<T>> RBuilder.Button(block: ButtonProps<T>.() -> Unit) {
    child(type = Button, props = jsObject(block))
}
