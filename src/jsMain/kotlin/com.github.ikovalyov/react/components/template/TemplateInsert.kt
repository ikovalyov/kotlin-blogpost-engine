package com.github.ikovalyov.react.components.template

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import com.benasher44.uuid.uuidFrom
import com.benasher44.uuid.uuidOf
import com.github.ikovalyov.model.Template
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.css.Color
import kotlinx.css.Display
import kotlinx.css.Float
import kotlinx.css.FontWeight
import kotlinx.css.LinearDimension
import kotlinx.css.QuotedString
import kotlinx.css.color
import kotlinx.css.content
import kotlinx.css.display
import kotlinx.css.float
import kotlinx.css.fontWeight
import kotlinx.css.height
import kotlinx.css.width
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.html.ButtonType
import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onSubmitFunction
import react.RBuilder
import react.RComponent
import react.PropsWithChildren
import react.State
import react.dom.attrs
import react.dom.button
import react.dom.defaultValue
import react.dom.fieldset
import react.dom.form
import react.dom.input
import react.setState
import styled.css
import styled.styledLabel
import styled.styledP
import styled.styledTextarea

external interface TemplateInsertProps : PropsWithChildren {
  var switchToListState: suspend () -> Unit
  var submitForm: suspend (t: Template) -> Unit
}

external interface TemplateInsertState : State {
  var currentTemplate: Template?
}

class TemplateInsert : RComponent<TemplateInsertProps, TemplateInsertState>() {
  override fun RBuilder.render() {
    if (state.currentTemplate == null) {
      state.currentTemplate = Template.create(uuid4(), "", "")
    }
    form {
      attrs {
        onSubmitFunction =
            {
              it.preventDefault()
              GlobalScope.async {
                console.info("Inserting new template into database")
                props.submitForm(state.currentTemplate!!)
              }
            }
      }
      fieldset {
        styledP {
          styledLabel {
            +"Id"
            css {
              color = Color("B4886B")
              fontWeight = FontWeight.bold
              display = Display.block
              width = LinearDimension("150px")
              float = Float.left
              after { content = QuotedString(":") }
            }
            attrs { this.attributes["htmlFor"] = "id" }
          }
          input {
            attrs {
              name = "id"
              defaultValue = ""
              onChangeFunction =
                  {
                    val id = it.target.asDynamic().value.toString()
                    val newTemplate = state.currentTemplate?.copy(id = uuidFrom(id))
                    setState { currentTemplate = newTemplate }
                  }
            }
          }
        }
        styledP {
          styledLabel {
            css {
              color = Color("B4886B")
              fontWeight = FontWeight.bold
              display = Display.block
              width = LinearDimension("150px")
              float = Float.left
              after { content = QuotedString(":") }
            }
            attrs { this.attributes["htmlFor"] = "lastModified" }
            +"Updated at"
          }
          input {
            attrs {
              name = "lastModified"
              defaultValue =
                  state.currentTemplate?.lastModified?.toLocalDateTime(TimeZone.UTC).toString()
              type = InputType.dateTimeLocal
              onChangeFunction =
                  {
                    val value = it.target.asDynamic().value.toString()
                    val ldt = LocalDateTime.parse(value)
                    val newTemplate =
                        state.currentTemplate?.copy(lastModified = ldt.toInstant(TimeZone.UTC))
                    setState { currentTemplate = newTemplate }
                  }
            }
          }
        }
        styledP {
          styledLabel {
            css {
              color = Color("B4886B")
              fontWeight = FontWeight.bold
              display = Display.block
              width = LinearDimension("150px")
              float = Float.left
              after { content = QuotedString(":") }
            }
            attrs { this.attributes["htmlFor"] = "template" }
            +"Template"
          }
          styledTextarea {
            css {
              height = LinearDimension("400px")
              width = LinearDimension("100%")
            }
            attrs {
              name = "template"
              onChangeFunction =
                  {
                    setState {
                      currentTemplate =
                          currentTemplate?.copy(body = it.target.asDynamic().value as String)
                    }
                  }
              defaultValue = ""
            }
          }
        }
      }
      button {
        attrs {
          text("Insert")
          name = "insert"
          type = ButtonType.submit
        }
      }
      button {
        attrs {
          text("Cancel")
          name = "insert"
          type = ButtonType.button
          onClickFunction = {
            GlobalScope.async {
              props.switchToListState()
            }
          }
        }
      }
    }
  }
}
