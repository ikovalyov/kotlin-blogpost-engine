package com.github.ikovalyov.react.components.bootstrap

import kotlinx.html.BUTTON
import kotlinx.html.DIV
import react.RBuilder
import styled.StyledDOMBuilder
import styled.css
import styled.styledDiv

sealed class Div {
    abstract operator fun invoke(builder: RBuilder, block: StyledDOMBuilder<DIV>.() -> Unit): Unit

    object ContainerFluidDiv: Div() {
        override operator fun invoke(builder: RBuilder, block: StyledDOMBuilder<DIV>.() -> Unit) {
            builder.styledDiv {
                css {
                    +"container-fluid"
                }
                block()
            }
        }
    }
}
