package com.github.ikovalyov.react.components.template

import com.github.ikovalyov.model.Template
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState

external interface TemplateEditProps : RProps {
    var template: Template
}

class TemplateEdit : RComponent<TemplateEditProps, RState>() {
    override fun RBuilder.render() {
        +"Edit template"
    }
}
