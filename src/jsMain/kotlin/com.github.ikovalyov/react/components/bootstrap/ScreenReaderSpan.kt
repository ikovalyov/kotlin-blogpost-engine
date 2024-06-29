package com.github.ikovalyov.react.components.bootstrap

import react.ChildrenBuilder
import react.dom.html.ReactHTML
import web.cssom.ClassName

fun ChildrenBuilder.screenReaderSpan(text: String) {
    ReactHTML.span {
        className = ClassName("sr-only")
        +text
    }
}
