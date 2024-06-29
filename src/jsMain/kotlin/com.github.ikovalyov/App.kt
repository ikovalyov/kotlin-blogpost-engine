package com.github.ikovalyov

import com.github.ikovalyov.react.components.bootstrap.Button
import com.github.ikovalyov.react.components.bootstrap.Div
import com.github.ikovalyov.react.components.bootstrap.Input
import com.github.ikovalyov.react.components.bootstrap.nav.collapseButton
import com.github.ikovalyov.react.components.bootstrap.nav.menuItem
import com.github.ikovalyov.react.components.bootstrap.screenReaderSpan
import emotion.react.css
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.form
import react.dom.html.ReactHTML.nav
import react.dom.html.ReactHTML.ul
import react.router.dom.Link
import web.cssom.ClassName
import web.cssom.px

const val NAVBAR_TOGGLER_ID = "navbarTogglerDemo01"
val App = FC<Props> {
    nav {
        className = ClassName("navbar navbar-expand-lg navbar-light bg-light")
        Div.ContainerFluidDiv(this) {
            collapseButton(target = NAVBAR_TOGGLER_ID)
            div {
                className = ClassName("collapse navbar-collapse")
                id = NAVBAR_TOGGLER_ID
                Link {
                    to = "/"
                    className = ClassName("navbar-brand")
                    +"Hidden brand"
                }
                ul {
                    className = ClassName("navbar-nav me-auto mb-2 mb-lg-0")

                    menuItem(href = "#", active = true, disabled = false) {
                        +"Home "
                        screenReaderSpan("(current)")
                    }
                    menuItem(
                        href = "${Api.BACKEND_ENDPOINT}/oauth/login/google",
                        active = false,
                        disabled = false,
                        reactLink = false,
                    ) { +"Login" }
                    menuItem(
                        href = "${Api.BACKEND_ENDPOINT}/logout",
                        active = false,
                        disabled = false,
                        reactLink = false,
                    ) { +"Logout" }
                    menuItem(href = "#", active = false, disabled = true) { +"Disabled" }
                }
                form {
                    css {
                        marginBlockEnd = 0.px
                    }
                    this.className = ClassName("${this.className} d-flex")
                    Input.SearchInput(name = "Search", this)
                    Button.ButtonOutlineSuccess(this) {
                        +"Search"
                    }
                }
            }
        }
    }
}
