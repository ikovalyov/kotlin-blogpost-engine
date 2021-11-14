package com.github.ikovalyov

import com.benasher44.uuid.uuid4
import com.github.ikovalyov.model.Template
import com.github.ikovalyov.model.security.Email
import com.github.ikovalyov.model.security.Password
import com.github.ikovalyov.model.security.ShortString
import com.github.ikovalyov.model.security.User
import com.github.ikovalyov.model.security.UserRole
import com.github.ikovalyov.react.components.bootstrap.Button
import com.github.ikovalyov.react.components.bootstrap.Div
import com.github.ikovalyov.react.components.bootstrap.Input
import com.github.ikovalyov.react.components.bootstrap.ScreenReaderSpan
import com.github.ikovalyov.react.components.bootstrap.nav.CollapseButton
import com.github.ikovalyov.react.components.bootstrap.nav.MenuItem
import com.github.ikovalyov.react.components.template.CrudComponent
import kotlinx.browser.document
import kotlinx.datetime.Clock
import kotlinx.html.id
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import react.dom.attrs
import react.dom.render
import styled.css
import styled.styledA
import styled.styledDiv
import styled.styledForm
import styled.styledNav
import styled.styledUl

@ExperimentalSerializationApi
suspend fun main() {
    render(document.getElementById("root")) {
        child(App::class) {}
        styledDiv {
            css {
                +"container"
            }
            styledDiv {
                css {
                    +"row flex-nowrap"
                }
                styledDiv {
                    css {
                        +"col-sm"
                    }
                    styledNav {
                        css {
                            +"navbar navbar-light"
                        }
                        styledUl {
                            css {
                                +"navbar-nav me-auto mb-2 mb-lg-0"
                            }
                            MenuItem(href = "#", active = true, disabled = false) {
                                +"Home "
                                ScreenReaderSpan("(current)")
                            }
                            MenuItem(href = "#", active = false, disabled = false) { +"Link" }
                            MenuItem(href = "#", active = false, disabled = true) { +"Disabled" }
                        }
                    }
                }
                styledDiv {
                    css {
                        +"col-auto"
                    }
                    CrudComponent<Template> {
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
                    CrudComponent<UserRole> {
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
                    CrudComponent<User> {
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
                }
            }
        }
    }
}
