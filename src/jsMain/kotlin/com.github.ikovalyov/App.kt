package com.github.ikovalyov

import com.github.ikovalyov.react.components.bootstrap.Button
import com.github.ikovalyov.react.components.bootstrap.nav.CollapseButton
import kotlinx.html.InputType
import kotlinx.html.id
import react.Props
import react.RBuilder
import react.RComponent
import react.State
import react.dom.attrs
import react.dom.h1
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
            styledDiv {
                css {
                    +"container-fluid"
                }
                CollapseButton(target = navbarTogglerId)
                styledDiv {
                    css {
                        +"collapse navbar-collapse"
                    }
                    attrs {
                        id = navbarTogglerId
                    }
                    styledA(href="#") {
                        css {
                            +"navbar-brand"
                        }
                        +"Hidden brand"
                    }
                    styledUl {
                        css {
                            +"navbar-nav me-auto mb-2 mb-lg-0"
                        }
                        styledLi {
                            css {
                                +"nav-item active"
                            }
                            styledA(href="#") {
                                css {
                                    +"nav-link"
                                }
                                +"Home "
                                styledSpan {
                                    css {
                                        +"sr-only"
                                    }
                                    +"(current)"
                                }
                            }
                        }
                        styledLi {
                            styledA(href="#") {
                                css {
                                    +"nav-link"
                                }
                                +"Link"
                            }
                        }
                        styledLi {
                            styledA(href="#") {
                                css {
                                    +"nav-link disabled"
                                }
                                +"Disabled"
                            }
                        }
                    }
                    styledForm {
                        css {
                            +"d-flex"
                            declarations["margin-block-end"] = "0px"
                        }
                        styledInput(type = InputType.search) {
                            css {
                                +"form-control me-2"
                            }
                            attrs {
                                placeholder = "Search"
                            }
                            attrs["aria-label"] = "Search"
                        }
                        Button.ButtonOutlineSuccess(this) { +"Search" }
                    }
                }
            }
        }

        h1 { +"Hello, React+Kotlin/JS!" }
    }
}
