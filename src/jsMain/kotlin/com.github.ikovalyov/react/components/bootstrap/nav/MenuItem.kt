package com.github.ikovalyov.react.components.bootstrap.nav

import csstype.ClassName
import org.w3c.dom.HTMLAnchorElement
import react.ChildrenBuilder
import react.dom.html.AnchorHTMLAttributes
import react.dom.html.ReactHTML.div

fun ChildrenBuilder.menuItem(href: String, active: Boolean, disabled: Boolean, block: AnchorHTMLAttributes<HTMLAnchorElement>.() -> Unit) {
    div {
        className = ClassName("nav-item")
        Anchor(href, active, disabled, block)
    }
}
