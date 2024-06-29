package com.github.ikovalyov.react.components.bootstrap

import react.ChildrenBuilder
import react.dom.html.ReactHTML
import web.cssom.ClassName
import web.html.ButtonType

sealed class Button {
    abstract operator fun invoke(builder: ChildrenBuilder, block: ChildrenBuilder.() -> Unit)

    object ButtonOutlineSuccess : Button() {
        override operator fun invoke(builder: ChildrenBuilder, block: ChildrenBuilder.() -> Unit) {
            with(builder) {
                ReactHTML.button {
                    className = ClassName("btn btn-outline-success")
                    type = ButtonType.submit
                    block()
                }
            }
        }
    }
}
