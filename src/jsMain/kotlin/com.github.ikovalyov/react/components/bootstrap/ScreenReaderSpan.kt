package com.github.ikovalyov.react.components.bootstrap

import kotlinx.html.SPAN
import react.RBuilder
import styled.StyledDOMBuilder
import styled.css
import styled.styledSpan

fun RBuilder.ScreenReaderSpan(text: String) {
    styledSpan {
        css {
            +"sr-only"
        }
        +text
    }
}