package com.github.ikovalyov.extenstion

import kotlinx.js.Object
import org.w3c.dom.Element
import react.Props
import react.dom.html.HTMLAttributes

var <T : Element>HTMLAttributes<T>.extraAttrs: Props
    @Deprecated(level = DeprecationLevel.HIDDEN, message = "write only")
    get() = error("write only")
    set(value) {
        for (key in Object.keys(value)) {
            asDynamic()[key] = value.asDynamic()[key]
        }
    }
