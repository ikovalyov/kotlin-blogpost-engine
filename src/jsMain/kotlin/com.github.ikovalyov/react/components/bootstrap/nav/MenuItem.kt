package com.github.ikovalyov.react.components.bootstrap.nav

import csstype.ClassName
import react.ChildrenBuilder
import react.dom.html.ReactHTML.div

fun ChildrenBuilder.menuItem(href: String, active: Boolean, disabled: Boolean, block: ChildrenBuilder.() -> Unit) {
    div {
        className = ClassName("nav-item")
        Anchor(href, active, disabled, block)
    }
}
