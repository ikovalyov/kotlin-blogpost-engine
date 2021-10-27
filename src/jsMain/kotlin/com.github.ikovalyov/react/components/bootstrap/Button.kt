package com.github.ikovalyov.react.components.bootstrap

import kotlinx.html.BUTTON
import kotlinx.html.ButtonType
import react.RBuilder
import react.dom.attrs
import styled.StyledDOMBuilder
import styled.css
import styled.styledButton

sealed class Button {
    abstract operator fun invoke(builder: RBuilder, block: StyledDOMBuilder<BUTTON>.() -> Unit): Unit

    object ButtonOutlineSuccess : Button() {
        override operator fun invoke(builder: RBuilder, block: StyledDOMBuilder<BUTTON>.() -> Unit) {
            builder.styledButton {
                css {
                    +"btn btn-outline-success"
                }
                attrs {
                    type = ButtonType.submit
                }
                block()
            }
        }
    }
}