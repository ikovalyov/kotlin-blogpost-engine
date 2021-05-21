package com.github.ikovalyov

import com.github.ikovalyov.react.components.template.TemplateList
import kotlinx.browser.document
import react.dom.*

suspend fun main() {
    render(document.getElementById("root")) {
        child(App::class) {}
        child(TemplateList::class) {}
    }
}
