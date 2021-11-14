package com.github.ikovalyov.react.components.bootstrap.nav

import kotlinx.html.ButtonType
import react.RBuilder
import styled.css
import styled.styledButton
import styled.styledSpan

fun RBuilder.CollapseButton(
    target: String
) = styledButton(type = ButtonType.button) {
    css {
        +"navbar-toggler"
    }
    attrs["data-bs-toggle"] = "collapse"
    attrs["data-bs-target"] = "#$target"
    attrs["aria-controls"] = target
    attrs["aria-expanded"] = "false"
    attrs["aria-label"] = "Toggle navigation"
    styledSpan {
        css {
            +"navbar-toggler-icon"
        }
    }
}
