package com.github.ikovalyov.react.components.template

import com.github.ikovalyov.model.Template
import com.github.ikovalyov.react.components.template.table.Button
import kotlinx.html.unsafe
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.div
import react.dom.h1
import react.dom.iframe
import react.dom.p
import react.dom.section

external interface TemplateViewProps : RProps {
    var template: Template
    var switchToListState: () -> Unit
}

class TemplateView : RComponent<TemplateViewProps, RState>() {
    override fun RBuilder.render() {
        div {
            section {
                h1 { +"Id" }
                p { +props.template.id }
            }
            section {
                h1 { +"Updated at" }
                p { +props.template.lastModified.toString() }
            }
            section {
                h1 { +"Template" }
                div { attrs.unsafe { +"<b>text</b>" } }
                iframe { attrs { set("srcDoc", props.template.template) } }
            }
            child(Button::class) {
                attrs {
                    onClick = { props.switchToListState() }
                    template = props.template
                    text = "Back to list"
                }
            }
        }
    }
}
