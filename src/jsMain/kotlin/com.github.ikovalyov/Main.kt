package com.github.ikovalyov

import com.benasher44.uuid.uuid4
import com.github.ikovalyov.model.Template
import com.github.ikovalyov.model.security.Email
import com.github.ikovalyov.model.security.Password
import com.github.ikovalyov.model.security.ShortString
import com.github.ikovalyov.model.security.User
import com.github.ikovalyov.model.security.UserRole
import com.github.ikovalyov.react.components.bootstrap.ScreenReaderSpan
import com.github.ikovalyov.react.components.bootstrap.nav.menuItem
import com.github.ikovalyov.react.components.template.CrudComponent
import com.github.ikovalyov.routes.Index
import csstype.ClassName
import csstype.attr
import kotlinx.browser.document
import kotlinx.datetime.Clock
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import react.FC
import react.Fragment
import react.Props
import react.create
import react.dom.client.createRoot
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.nav
import react.dom.html.ReactHTML.ul
import react.router.Route
import react.router.Routes
import react.router.dom.BrowserRouter

@OptIn(ExperimentalSerializationApi::class)
fun main() {
    val container = document.getElementById("root")!!
    val root = createRoot(container)
    root.render(
        Fragment.create {
            BrowserRouter {
                App {

                }
                Routes {
                    Route {
                        index = true
                        element = Index.create()
                        path = "/"
                    }
                    Route {
                        path = "/login"
                    }
                }
            }
        }
    )
}
