package com.github.ikovalyov.react.components.bootstrap

import com.github.ikovalyov.react.components.bootstrap.nav.ariaLabel
import react.ChildrenBuilder
import react.dom.html.ReactHTML
import web.cssom.ClassName
import web.html.InputType

sealed class Input {
    abstract operator fun invoke(name: String, builder: ChildrenBuilder)

    object SearchInput : Input() {
        override operator fun invoke(name: String, builder: ChildrenBuilder) {
            with(builder) {
                ReactHTML.input {
                    className = ClassName("form-control me-2")
                    type = InputType.search
                    placeholder = name
                    ariaLabel = name
                }
            }
        }
    }
}
