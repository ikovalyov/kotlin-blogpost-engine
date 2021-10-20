package com.github.ikovalyov

import kotlinx.html.ButtonType
import kotlinx.html.id
import react.Props
import react.RBuilder
import react.RComponent
import react.State
import react.dom.a
import react.dom.attrs
import react.dom.h1
import react.dom.nav
import styled.css
import styled.styledA
import styled.styledButton
import styled.styledDiv
import styled.styledLi
import styled.styledNav
import styled.styledSpan
import styled.styledUl

class App : RComponent<Props, State>() {
    override fun RBuilder.render() {
        styledNav {
            css {
                +"navbar navbar-inverse navbar-fixed-top"
            }
            styledDiv {
                css {
                    +"container"
                }
                styledDiv {
                    css {
                        +"navbar-header"
                    }
                    styledButton {
                        css {
                            +"navbar-toggle collapsed"
                        }
                        attrs {
                            type = ButtonType.button
                        }
                        attrs["data-toggle"] = "collapse"
                        attrs["data-target"] = "#navbar"
                        attrs["aria-expanded"] = "false"
                        attrs["aria-controls"] = "navbar"
                        styledSpan {
                            css {
                                +"sr-only"
                            }
                            +"Toggle navigation"
                        }
                        repeat(3) {
                            styledSpan {
                                css {
                                    +"icon-bar"
                                }
                            }
                        }
                    }
                    styledA {
                        css {
                            +"navbar-brand"
                        }
                        attrs {
                            href = "#"
                        }
                        +"Project name"
                    }
                }
                styledDiv {
                    css {
                        +"collapse navbar-collapse"
                    }
                    attrs {
                        id = "navbar"
                    }
                    styledUl {
                        css {
                            +"nav navbar-nav"
                        }
                        styledLi {
                            css {
                                +"active"
                            }
                            a {
                                attrs {
                                    href = "#"
                                }
                                +"Home"
                            }
                        }
                        styledLi {
                            a {
                                attrs {
                                    href = "#"
                                }
                                +"About"
                            }
                        }
                        styledLi {
                            a {
                                attrs {
                                    href = "#"
                                }
                                +"Contact"
                            }
                        }
                    }
                }
            }
        }

        h1 { +"Hello, React+Kotlin/JS!" }
    }
}
