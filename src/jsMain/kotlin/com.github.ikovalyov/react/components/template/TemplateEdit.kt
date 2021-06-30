package com.github.ikovalyov.react.components.template

import com.github.ikovalyov.model.Template
import com.github.ikovalyov.react.components.template.table.Button
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
import kotlinx.datetime.Instant
import kotlinx.html.ButtonType
import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onSubmitFunction
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.attrs
import react.dom.defaultValue
import react.dom.fieldset
import react.dom.form
import react.dom.input
import react.setState
import styled.css
import styled.styledLabel
import styled.styledP
import styled.styledTextarea

external interface TemplateEditProps : RProps {
    var template: Template
    var switchToListState: suspend () -> Unit
    var submitForm: suspend (t: Template) -> Unit
}

external interface TemplateState : RState {
    var currentTemplate: Template?
}

class TemplateEdit : RComponent<TemplateEditProps, TemplateState>() {
    override fun RBuilder.render() {
        form {
            attrs {
                onSubmitFunction =
                    {
                        it.preventDefault()
                        GlobalScope.async {
                            console.info("submit form ${state.currentTemplate}")
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
                            value = props.template.id
                            readonly = true
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
                            defaultValue = props.template.lastModified.epochSeconds.toString()
                            type = InputType.number
                            onChangeFunction =
                                {
                                    val value = it.target.asDynamic().value
                                    setState {
                                        val template = currentTemplate ?: props.template
                                        currentTemplate =
                                            template.copy(
                                                lastModified =
                                                    Instant.fromEpochSeconds(
                                                        value.toLong() as Long))
                                    }
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
                                        val template = currentTemplate ?: props.template
                                        currentTemplate =
                                            template.copy(
                                                template = it.target.asDynamic().value as String)
                                    }
                                }
                            defaultValue = props.template.template
                        }
                    }
                }
            }
            child(Button::class) {
                attrs {
                    template = props.template
                    text = "Update"
                    type = ButtonType.submit
                }
            }
            child(Button::class) {
                attrs {
                    onClick = { GlobalScope.async { props.switchToListState() } }
                    template = props.template
                    text = "Cancel"
                }
            }
        }
    }
}
