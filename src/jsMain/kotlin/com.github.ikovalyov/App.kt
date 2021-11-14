package com.github.ikovalyov

import com.github.ikovalyov.react.components.bootstrap.Button
import com.github.ikovalyov.react.components.bootstrap.Div
import com.github.ikovalyov.react.components.bootstrap.Input
import com.github.ikovalyov.react.components.bootstrap.ScreenReaderSpan
import com.github.ikovalyov.react.components.bootstrap.nav.CollapseButton
import com.github.ikovalyov.react.components.bootstrap.nav.MenuItem
import kotlinx.html.InputType
import kotlinx.html.SPAN
import kotlinx.html.id
import react.Props
import react.RBuilder
import react.RComponent
import react.ReactNode
import react.State
import react.dom.attrs
import react.dom.h1
import styled.StyledDOMBuilder
import styled.css
import styled.styledA
import styled.styledDiv
import styled.styledForm
import styled.styledInput
import styled.styledLi
import styled.styledNav
import styled.styledSpan
import styled.styledUl

class App : RComponent<Props, State>() {
    companion object {
        const val navbarTogglerId = "navbarTogglerDemo01"
    }

    override fun RBuilder.render() {
        styledNav {
            css {
                +"navbar navbar-expand-lg navbar-light bg-light"
            }
            Div.ContainerFluidDiv(this) {
                CollapseButton(target = navbarTogglerId)
                styledDiv {
                    css {
                        +"collapse navbar-collapse"
                    }
                    attrs {
                        id = navbarTogglerId
                    }
                    styledA(href = "#") {
                        css {
                            +"navbar-brand"
                        }
                        +"Hidden brand"
                    }
                    styledUl {
                        css {
                            +"navbar-nav me-auto mb-2 mb-lg-0"
                        }
                        MenuItem(href = "#", active = true, disabled = false) {
                            +"Home "
                            ScreenReaderSpan("(current)")
                        }
                        MenuItem(href = "#", active = false, disabled = false) { +"Link" }
                        MenuItem(href = "#", active = false, disabled = true) { +"Disabled" }
                    }
                    styledForm {
                        css {
                            +"d-flex"
                            declarations["margin-block-end"] = "0px"
                        }
                        Input.SearchInput(name = "Search", this)
                        Button.ButtonOutlineSuccess(this) { +"Search" }
                    }
                }
            }
        }
    }
}
