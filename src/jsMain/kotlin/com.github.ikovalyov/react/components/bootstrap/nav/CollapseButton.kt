package com.github.ikovalyov.react.components.bootstrap.nav

import csstype.ClassName
import react.ChildrenBuilder
import react.dom.html.ButtonType
import react.dom.html.HTMLAttributes
import react.dom.html.ReactHTML
import react.dom.html.ReactHTML.span

var HTMLAttributes<*>.dataBsToggle: String?
    get() = asDynamic()["data-bs-toggle"]
    set(value) {
        asDynamic()["data-bs-toggle"] = value
    }

var HTMLAttributes<*>.dataBsTarget: String?
    get() = asDynamic()["data-bs-toggle"]
    set(value) {
        asDynamic()["data-bs-toggle"] = value
    }
var HTMLAttributes<*>.ariaControls: String?
    get() = asDynamic()["aria-controls"]
    set(value) {
        asDynamic()["aria-controls"] = value
    }
var HTMLAttributes<*>.ariaExpanded: String?
    get() = asDynamic()["aria-expanded"]
    set(value) {
        asDynamic()["aria-expanded"] = value
    }
var HTMLAttributes<*>.ariaLabel: String?
    get() = asDynamic()["aria-label"]
    set(value) {
        asDynamic()["aria-label"] = value
    }

fun ChildrenBuilder.CollapseButton(target: String) = ReactHTML.button {
    type = ButtonType.button
    className = ClassName("navbar-toggler")
    dataBsToggle = "collapse"
    dataBsTarget = "#$target"
    ariaControls = target
    ariaExpanded = "false"
    ariaLabel = "Toggle navigation"
    span {
        className = ClassName("navbar-toggler-icon")
    }
}
