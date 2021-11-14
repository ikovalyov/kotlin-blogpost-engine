package com.github.ikovalyov.react.components.bootstrap.nav

import kotlinx.html.A
import react.RBuilder
import styled.StyledDOMBuilder
import styled.css
import styled.styledLi

fun RBuilder.MenuItem(
    href: String,
    active: Boolean,
    disabled: Boolean,
    block: StyledDOMBuilder<A>.() -> Unit
) {
    styledLi {
        css {
            +"nav-item"
        }
        Anchor(href, active, disabled, block)
    }
}
