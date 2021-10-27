package com.github.ikovalyov.react.components.bootstrap.nav

import kotlinx.html.A
import kotlinx.html.BUTTON
import kotlinx.html.LI
import react.RBuilder
import react.ReactNode
import styled.StyledDOMBuilder
import styled.css
import styled.styledA
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
            if (active) {
                +"active"
            }
            if (disabled) {
                +"disabled"
            }
        }
        styledA(href=href) {
            css {
                +"nav-link"
            }
            apply(block)
        }
    }
}