package com.github.ikovalyov.react.components.template.table

import com.github.ikovalyov.model.Template
import kotlinx.html.ButtonType
import kotlinx.html.js.onClickFunction
import react.RBuilder
import react.RComponent
import react.PropsWithChildren
import react.State
import react.dom.attrs
import react.dom.button

typealias OnClickFunc = (Template) -> Unit

external interface ButtonProps : PropsWithChildren {
  var template: Template
  var onClick: OnClickFunc?
  var text: String
  var type: ButtonType?
}

class Button : RComponent<ButtonProps, State>() {
  override fun RBuilder.render() {
    val properties = props
    button {
      attrs {
        text(properties.text)
        value = properties.template.id.toString()
        name = "edit"
        type = properties.type ?: ButtonType.button
        onClickFunction = { properties.onClick?.let { it(properties.template) } }
      }
    }
  }
}
