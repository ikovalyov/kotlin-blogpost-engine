package com.github.ikovalyov

import kotlinx.html.ButtonType
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
import styled.styledButton
import styled.styledDiv
import styled.styledForm
import styled.styledInput
import styled.styledLi
import styled.styledNav
import styled.styledSpan
import styled.styledUl

class App : RComponent<Props, State>() {
    override fun RBuilder.render() {
        styledNav {
            css {
                +"navbar navbar-expand-lg navbar-light bg-light"
            }
            styledDiv {
                css {
                    +"container-fluid"
                }
                styledButton {
                    css {
                        +"navbar-toggler"
                    }
                    attrs {
                        type = ButtonType.button
                    }
                    attrs["data-bs-toggle"] = "collapse"
                    attrs["data-bs-target"] = "#navbarTogglerDemo01"
                    attrs["aria-controls"] = "navbarTogglerDemo01"
                    attrs["aria-expanded"] = "false"
                    attrs["aria-label"] = "Toggle navigation"
                    styledSpan {
                        css {
                            +"navbar-toggler-icon"
                        }
                    }
                }
                styledDiv {
                    css {
                        +"collapse navbar-collapse"
                    }
                    attrs {
                        id = "navbarTogglerDemo01"
                    }
                    styledA {
                        css {
                            +"navbar-brand"
                        }
                        attrs {
                            href = "#"
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
                            styledA {
                                css {
                                    +"nav-link"
                                }
                                attrs {
                                    href = "#"
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
                            styledA {
                                css {
                                    +"nav-link"
                                }
                                attrs {
                                    href = "#"
                                }
                                +"Link"
                            }
                        }
                        styledLi {
                            styledA {
                                css {
                                    +"nav-link disabled"
                                }
                                attrs {
                                    href = "#"
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
                        styledInput {
                            css {
                                +"form-control me-2"
                            }
                            attrs {
                                type = InputType.search
                                placeholder = "Search"
                            }
                            attrs["aria-label"] = "Search"
                        }
                        styledButton {
                            css {
                                +"btn btn-outline-success"
                            }
                            attrs {
                                type = ButtonType.submit
                            }
                            +"Search"
                        }
                    }
                }
            }
        }

        h1 { +"Hello, React+Kotlin/JS!" }
    }
}
