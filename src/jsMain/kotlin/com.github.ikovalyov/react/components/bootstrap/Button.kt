package com.github.ikovalyov.react.components.bootstrap

import csstype.ClassName
import react.ChildrenBuilder
import react.dom.html.ButtonType
import react.dom.html.ReactHTML

sealed class Button {
    abstract operator fun invoke(builder: ChildrenBuilder, block: ChildrenBuilder.() -> Unit): Unit

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
