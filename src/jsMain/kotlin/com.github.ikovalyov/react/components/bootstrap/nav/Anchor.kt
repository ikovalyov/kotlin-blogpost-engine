package com.github.ikovalyov.react.components.bootstrap.nav

import csstype.ClassName
import org.w3c.dom.HTMLAnchorElement
import react.ChildrenBuilder
import react.dom.html.AnchorHTMLAttributes
import react.dom.html.ReactHTML.a

fun ChildrenBuilder.Anchor(hrefString: String, active: Boolean, disabled: Boolean, block: ChildrenBuilder.() -> Unit) {
    a {
        this.href = hrefString
        val classes = buildList {
            add("nav-link")
            if (active) {
                add("active")
            }

            if (disabled) {
                add("disabled")
            }
        }
        val classesString = classes.joinToString(separator = " " )
        className = ClassName(classesString)
        this.apply(block)
    }
}
