package com.github.ikovalyov.react.components.bootstrap.nav

import csstype.ClassName
import react.ChildrenBuilder
import react.dom.html.ReactHTML.div

fun ChildrenBuilder.menuItem(href: String, active: Boolean, disabled: Boolean, reactLink: Boolean = true, block: ChildrenBuilder.() -> Unit) {
    div {
        className = ClassName("nav-item")
        if (reactLink) {
            ReactAnchor(href, active, disabled, block)
        } else {
            Anchor(href, active, disabled, block)
        }
    }
}
