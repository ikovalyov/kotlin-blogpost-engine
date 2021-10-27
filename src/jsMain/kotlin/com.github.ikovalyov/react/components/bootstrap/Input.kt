package com.github.ikovalyov.react.components.bootstrap

import kotlinx.html.BUTTON
import kotlinx.html.ButtonType
import kotlinx.html.INPUT
import kotlinx.html.InputType
import react.RBuilder
import react.dom.attrs
import styled.StyledDOMBuilder
import styled.css
import styled.styledButton
import styled.styledInput

sealed class Input {
    abstract operator fun invoke(name: String, builder: RBuilder): Unit

    object SearchInput : Input() {
        override operator fun invoke(name: String, builder: RBuilder) {
            builder.styledInput {
                css {
                    +"form-control me-2"
                }
                attrs {
                    type = InputType.search
                }
                attrs {
                    placeholder = name
                }
                attrs["aria-label"] = name
            }
        }
    }
}