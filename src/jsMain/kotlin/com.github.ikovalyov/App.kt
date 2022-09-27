package com.github.ikovalyov

import com.github.ikovalyov.react.components.bootstrap.Button
import com.github.ikovalyov.react.components.bootstrap.Div
import com.github.ikovalyov.react.components.bootstrap.Input
import com.github.ikovalyov.react.components.bootstrap.ScreenReaderSpan
import com.github.ikovalyov.react.components.bootstrap.nav.CollapseButton
import com.github.ikovalyov.react.components.bootstrap.nav.menuItem
import csstype.ClassName
import csstype.px
import emotion.react.css
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.form
import react.dom.html.ReactHTML.nav
import react.dom.html.ReactHTML.ul
import react.router.dom.Link

const val navbarTogglerId = "navbarTogglerDemo01"
val App = FC<Props> {
    nav {
        className = ClassName("navbar navbar-expand-lg navbar-light bg-light")
        Div.ContainerFluidDiv(this) {
            CollapseButton(target = navbarTogglerId)
            div {
                className = ClassName("collapse navbar-collapse")
                id = navbarTogglerId
                Link {
                    to = "/"
//                    href = "#"
                    className = ClassName("navbar-brand")
                    +"Hidden brand"
                }
                ul {
                    className = ClassName("navbar-nav me-auto mb-2 mb-lg-0")

                    menuItem(href = "#", active = true, disabled = false) {
                        +"Home "
                        ScreenReaderSpan("(current)")
                    }
                    menuItem(href = "${Api.backendEndpoint}/oauth/login/google", active = false, disabled = false, reactLink = false) { +"Login" }
                    menuItem(href = "${Api.backendEndpoint}/logout", active = false, disabled = false, reactLink = false) { +"Logout" }
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
