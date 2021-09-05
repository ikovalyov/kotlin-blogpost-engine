package com.github.ikovalyov.react.components.template.table

import com.github.ikovalyov.model.markers.IdInterface
import kotlinx.html.ButtonType
import kotlinx.html.js.onClickFunction
import react.RBuilder
import react.RComponent
import react.PropsWithChildren
import react.State
import react.dom.attrs
import react.dom.button

typealias OnClickFunc<T> = (T) -> Unit

external interface ButtonProps<T: Any> : PropsWithChildren {
  var body: T
  var onClick: OnClickFunc<T>
  var text: String
  var type: ButtonType?
}

class Button : RComponent<ButtonProps<IdInterface>, State>() {
  override fun RBuilder.render() {
    val properties = props
    button {
      attrs {
        text(properties.text)
        value = properties.body.id.toString()
        name = "edit"
        type = properties.type ?: ButtonType.button
        onClickFunction = {
          properties.onClick?.let {
            it(properties.body)
          }
        }
      }
    }
  }
}
