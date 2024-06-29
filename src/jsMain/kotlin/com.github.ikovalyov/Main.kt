package com.github.ikovalyov

import com.github.ikovalyov.model.security.service.SecurityService
import com.github.ikovalyov.model.service.TemplateService
import com.github.ikovalyov.model.service.UserService
import com.github.ikovalyov.routes.Index
import kotlinx.serialization.ExperimentalSerializationApi
import react.FC
import react.Fragment
import react.create
import react.dom.client.createRoot
import react.dom.html.ReactHTML
import react.router.RouteObject
import react.router.RouterProvider
import react.router.dom.createBrowserRouter
import web.cssom.ClassName
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

    val appRouter = createBrowserRouter(
        arrayOf(
            RouteObject(
                path = "/",
                element = Fragment.create {
                    +App.create()
                    ReactHTML.div {
                        className = ClassName("container-fluid")
                        +Index.create {
                            this.currentUser = currentUser
                            this.userList = allUsers
                            this.templateList = allTemplates
                        }
                    }
                },
            ),
        ),
    )

    val reactRouterDomApp = FC {
        RouterProvider {
            router = appRouter
        }
    }
    root.render(
        reactRouterDomApp.create(),
    )
}
