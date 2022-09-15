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
import csstype.ClassName
import kotlinx.browser.document
import kotlinx.datetime.Clock
import kotlinx.js.jso
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

@OptIn(ExperimentalSerializationApi::class)
suspend fun main() {
    val container = document.getElementById("root")!!
    val root = createRoot(container)
    root.render(
        Fragment.create {
            App()
            FC<Props> {
                div {
                    className = ClassName("container")
                    div {
                        className = ClassName("row flex-nowrap")
                        div {
                            className = ClassName("col-sm")
                            nav {
                                className = ClassName("navbar navbar-light")
                                ul {
                                    className = ClassName("navbar-nav me-auto mb-2 mb-lg-0")
                                    menuItem(href = "#", active = true, disabled = false) {
                                        +"Home"
                                        ScreenReaderSpan("(current)")
                                    }
                                    menuItem(href = "#", active = false, disabled = false) {
                                        +"Link"
                                    }
                                    menuItem(href = "#", active = false, disabled = true) {
                                        +"Disabled"
                                    }
                                }
                            }
                        }
                        div {
                            className = ClassName("col-auto")
                            CrudComponent<Template>(
                                props = jso {
                                    decodeItem = {
                                        Json.decodeFromString(Template.serializer(), it)
                                    }
                                    decodeItems = {
                                        Json.decodeFromString(ListSerializer(Template.serializer()), it)
                                    }
                                    apiUri = Api.templateUrl
                                    factory = {
                                        Template(id = uuid4(), "", "")
                                    }
                                }
                            )
                            CrudComponent<UserRole>(
                                jso {
                                    decodeItem = {
                                        Json.decodeFromString(UserRole.serializer(), it)
                                    }
                                    decodeItems = {
                                        Json.decodeFromString(ListSerializer(UserRole.serializer()), it)
                                    }
                                    apiUri = Api.userRoleUrl
                                    factory = {
                                        UserRole(uuid4(), Clock.System.now(), "")
                                    }
                                }
                            )
                            CrudComponent<User>(
                                jso {
                                    decodeItem = {
                                        Json.decodeFromString(User.serializer(), it)
                                    }
                                    decodeItems = {
                                        Json.decodeFromString(ListSerializer(User.serializer()), it)
                                    }
                                    apiUri = Api.userUrl
                                    factory = {
                                        User(uuid4(), Email(ShortString("")), false, "", emptyList(), Password(ShortString("")))
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}
