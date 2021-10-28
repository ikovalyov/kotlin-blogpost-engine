package com.github.ikovalyov.react.components.bootstrap.nav

import kotlinx.html.A
import react.RBuilder
import styled.StyledDOMBuilder
import styled.css
import styled.styledA

fun RBuilder.Anchor(
    href: String,
    active: Boolean,
    disabled: Boolean,
    block: StyledDOMBuilder<A>.() -> Unit
) {
    styledA(href = href) {
        css {
            +"nav-link"
            if (active) {
                +"active"
            }
            if (disabled) {
                +"disabled"
            }
        }
        apply(block)
    }
}
