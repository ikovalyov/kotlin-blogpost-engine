package com.github.ikovalyov.react.components.template.table

import com.github.ikovalyov.model.markers.IEditable
import js.objects.jso
import react.ChildrenBuilder
import react.FC
import react.PropsWithChildren
import react.dom.html.ReactHTML.button
import web.html.ButtonType

external interface ButtonProps<T : IEditable> : PropsWithChildren {
    var body: T
    var onClick: ((T) -> Unit)?
    var text: String
    var type: ButtonType?
}

private fun <T : IEditable> ChildrenBuilder.Button(props: ButtonProps<T>) {
    button {
        +props.text
        value = props.body.id.toString()
        name = "edit"
        type = props.type ?: ButtonType.button
        onClick = { props.onClick?.invoke(props.body) }
    }
}

private val Button: FC<ButtonProps<*>> = FC { props ->
    Button(props)
}

fun <T : IEditable> ChildrenBuilder.Button(block: ButtonProps<T>.() -> Unit) {
    child(type = Button, props = jso(block))
}
