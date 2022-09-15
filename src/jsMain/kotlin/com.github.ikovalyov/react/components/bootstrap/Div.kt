package com.github.ikovalyov.react.components.bootstrap

import csstype.ClassName
import react.ChildrenBuilder
import react.dom.html.ReactHTML

sealed class Div {
    abstract operator fun invoke(builder: ChildrenBuilder, block: ChildrenBuilder.() -> Unit): Unit

    object ContainerFluidDiv : Div() {
        override operator fun invoke(builder: ChildrenBuilder, block: ChildrenBuilder.() -> Unit) {
            with(builder) {
                ReactHTML.div {
                    className = ClassName("container-fluid")
                    block()
                }
            }
        }
    }
}
