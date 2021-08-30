package com.github.ikovalyov.extenstion

import kotlinext.js.Object
import react.PropsWithChildren
import react.dom.RDOMBuilder
import react.dom.setProp

var RDOMBuilder<*>.extraAttrs: PropsWithChildren
  @Deprecated(level = DeprecationLevel.HIDDEN, message = "write only") get() = error("write only")
  set(value) {
    for (key in Object.keys(value)) {
      setProp(key, value.asDynamic()[key])
    }
  }
