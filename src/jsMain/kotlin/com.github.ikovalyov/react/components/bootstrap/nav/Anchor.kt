package com.github.ikovalyov.react.components.bootstrap.nav

import csstype.ClassName
import react.ChildrenBuilder
import react.dom.html.ReactHTML.a
import react.router.dom.Link

fun ChildrenBuilder.Anchor(hrefString: String, active: Boolean, disabled: Boolean, block: ChildrenBuilder.() -> Unit) {
    Link {
        to = hrefString
        val classes = buildList {
            add("nav-link")
            if (active) {
                add("active")
            }

            if (disabled) {
                add("disabled")
            }
        }
        val classesString = classes.joinToString(separator = " ")
        className = ClassName(classesString)
        this.apply(block)
    }
}
