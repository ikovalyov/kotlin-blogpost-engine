package com.github.ikovalyov.react.components.bootstrap.nav

import csstype.ClassName
import kotlinx.browser.window
import react.ChildrenBuilder
import react.router.dom.Link

fun ChildrenBuilder.ReactAnchor(hrefString: String, active: Boolean, disabled: Boolean, block: ChildrenBuilder.() -> Unit) {
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
fun ChildrenBuilder.Anchor(hrefString: String, active: Boolean, disabled: Boolean, block: ChildrenBuilder.() -> Unit) {
    Link {
        to = "#"
        val classes = buildList {
            add("nav-link")
            if (active) {
                add("active")
            }

            if (disabled) {
                add("disabled")
            }
        }
        onClick = {
            console.log(hrefString)
            window.location.href = hrefString
        }
        val classesString = classes.joinToString(separator = " ")
        className = ClassName(classesString)
        this.apply(block)
    }
}
