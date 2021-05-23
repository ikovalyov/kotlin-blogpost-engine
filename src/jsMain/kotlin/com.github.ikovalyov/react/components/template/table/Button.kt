package com.github.ikovalyov.react.components.template.table

import com.github.ikovalyov.model.Template
import kotlinx.html.js.onClickFunction
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.button

external interface ButtonProps : RProps {
    var template: Template
    var onClick: (Template) -> Unit
    var text: String
}

class Button : RComponent<ButtonProps, RState>() {
    override fun RBuilder.render() {
        val properties = props
        button {
            attrs {
                text(properties.text)
                value = properties.template.id
                name = "edit"
                onClickFunction =
                    {
                        properties.onClick(properties.template)
                    }
            }
        }
    }
}
