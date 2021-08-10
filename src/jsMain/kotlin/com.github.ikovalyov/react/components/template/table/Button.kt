package com.github.ikovalyov.react.components.template.table

import com.github.ikovalyov.model.Template
import kotlinx.html.ButtonType
import kotlinx.html.js.onClickFunction
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.attrs
import react.dom.button

typealias OnClickFunc = (Template) -> Unit

external interface ButtonProps : RProps {
    var template: Template
    var onClick: OnClickFunc?
    var text: String
    var type: ButtonType?
}

class Button : RComponent<ButtonProps, RState>() {
    override fun RBuilder.render() {
        val properties = props
        button {
            attrs {
                text(properties.text)
                value = properties.template.id
                name = "edit"
                type = properties.type ?: ButtonType.button
                onClickFunction = { properties.onClick?.let { it(properties.template) } }
            }
        }
    }
}
