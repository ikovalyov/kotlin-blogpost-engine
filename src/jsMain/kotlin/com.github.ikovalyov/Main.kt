package com.github.ikovalyov

import com.github.ikovalyov.routes.Index
import csstype.ClassName
import kotlinx.browser.document
import kotlinx.js.URLSearchParams
import react.Fragment
import react.create
import react.dom.client.createRoot
import react.dom.html.ReactHTML
import react.router.Route
import react.router.Routes
import react.router.dom.BrowserRouter

fun main() {
    val container = document.getElementById("root")!!
    val root = createRoot(container)
    root.render(
        Fragment.create {
            BrowserRouter {
                App { }
                ReactHTML.div {
                    className = ClassName("container-fluid")
                    Routes {
                        Route {
                            index = true
                            element = Index.create()
                            path = "/"
                        }
                    }
                }
            }
        }
    )
}
