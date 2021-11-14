package com.github.ikovalyov.react.components.bootstrap

import react.RBuilder
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
