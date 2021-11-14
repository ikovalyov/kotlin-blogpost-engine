package com.github.ikovalyov.react.components.bootstrap

import kotlinx.html.InputType
import react.RBuilder
import react.dom.attrs
import styled.css
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
