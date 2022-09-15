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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import react.Component
import react.FC
import react.Fragment
import react.Props
import react.ReactNode
import react.State
import react.create
import react.dom.html.ReactHTML.a
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.form
import react.dom.html.ReactHTML.nav
import react.dom.html.ReactHTML.ul
import kotlin.coroutines.CoroutineContext

class App : Component<Props, State>(), CoroutineScope {
    private var job = Job()
    override val coroutineContext: CoroutineContext
        get() = job
    companion object {
        const val navbarTogglerId = "navbarTogglerDemo01"
    }

    override fun render(): ReactNode {
        return Fragment.create {
            FC<Props> {
                nav {
                    className = ClassName("navbar navbar-expand-lg navbar-light bg-light")
                }
                Div.ContainerFluidDiv(this) {
                    CollapseButton(target = navbarTogglerId)
                    div {
                        className = ClassName("collapse navbar-collapse")
                        id = navbarTogglerId
                        a {
                            href = "#"
                            className = ClassName("navbar-brand")
                            +"Hidden brand"
                        }
                        ul {
                            className = ClassName("navbar-nav me-auto mb-2 mb-lg-0")
                            menuItem(href = "#", active = true, disabled = false) {
                                +"Home "
                                ScreenReaderSpan("(current)")
                            }
                            menuItem(href = "#", active = false, disabled = false) { +"Link" }
                            menuItem(href = "#", active = false, disabled = true) { +"Disabled" }
                        }
                        form {
                            className = ClassName("d-flex")
                            css {
                                marginBlockEnd = 0.px
                            }
                            Input.SearchInput(name = "Search", this)
                            Button.ButtonOutlineSuccess(this) { +"Search" }
                        }
                    }
                }
            }
        }
    }
}
