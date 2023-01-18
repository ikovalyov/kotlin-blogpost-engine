package com.github.ikovalyov

import com.github.ikovalyov.model.security.service.SecurityService
import com.github.ikovalyov.model.service.TemplateService
import com.github.ikovalyov.model.service.UserService
import com.github.ikovalyov.routes.Index
import csstype.ClassName
import kotlinx.serialization.ExperimentalSerializationApi
import react.Fragment
import react.create
import react.dom.client.createRoot
import react.dom.html.ReactHTML
import react.router.Route
import react.router.Routes
import react.router.dom.BrowserRouter
import web.dom.document

@OptIn(ExperimentalSerializationApi::class)
suspend fun main() {
    val container = document.getElementById("root")!!
    val root = createRoot(container)
    val securityService = SecurityService()
    val currentUser = securityService.getCurrentUser()
    val userService = UserService()
    val allUsers = userService.getAllUsers()
    val templateService = TemplateService()
    val allTemplates = templateService.getAllTemplates()
    root.render(
        Fragment.create {
            BrowserRouter {
                App { }
                ReactHTML.div {
                    className = ClassName("container-fluid")
                    Routes {
                        Route {
                            index = true
                            element = Index.create {
                                this.currentUser = currentUser
                                this.userList = allUsers
                                this.templateList = allTemplates
                            }
                            path = "/"
                        }
                    }
                }
            }
        }
    )
}
