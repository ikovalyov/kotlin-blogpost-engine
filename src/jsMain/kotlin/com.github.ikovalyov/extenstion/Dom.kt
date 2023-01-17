package com.github.ikovalyov.extenstion

import js.core.Object
import react.Props
import react.dom.html.HTMLAttributes
import web.dom.Element

var <T : Element>HTMLAttributes<T>.extraAttrs: Props
    @Deprecated(level = DeprecationLevel.HIDDEN, message = "write only")
    get() = error("write only")
    set(value) {
        for (key in Object.keys(value)) {
            asDynamic()[key] = value.asDynamic()[key]
        }
    }
