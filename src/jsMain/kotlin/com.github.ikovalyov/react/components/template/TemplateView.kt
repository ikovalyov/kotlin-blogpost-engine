package com.github.ikovalyov.react.components.template

import com.github.ikovalyov.model.Template
import com.github.ikovalyov.react.components.template.table.Button
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.html.unsafe
import react.RBuilder
import react.RComponent
import react.RProps
import react.State
import react.dom.attrs
import react.dom.div
import react.dom.h1
import react.dom.iframe
import react.dom.p
import react.dom.section

external interface TemplateViewProps : RProps {
  var template: Template
  var switchToListState: suspend () -> Unit
}

class TemplateView : RComponent<TemplateViewProps, State>() {
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
          onClick = { GlobalScope.async { props.switchToListState() } }
          template = props.template
          text = "Back to list"
        }
      }
    }
  }
}
