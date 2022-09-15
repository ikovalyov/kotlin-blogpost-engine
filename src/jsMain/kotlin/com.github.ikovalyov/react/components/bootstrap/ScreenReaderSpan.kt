package com.github.ikovalyov.react.components.bootstrap

import csstype.ClassName
import react.ChildrenBuilder
import react.dom.html.ReactHTML

fun ChildrenBuilder.ScreenReaderSpan(text: String) {
    ReactHTML.span {
        className = ClassName("sr-only")
        +text
    }
}
