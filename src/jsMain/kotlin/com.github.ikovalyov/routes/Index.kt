package com.github.ikovalyov.routes

import com.benasher44.uuid.uuid4
import com.github.ikovalyov.Api
import com.github.ikovalyov.model.Article
import com.github.ikovalyov.model.Template
import com.github.ikovalyov.model.security.Email
import com.github.ikovalyov.model.security.Password
import com.github.ikovalyov.model.security.ShortString
import com.github.ikovalyov.model.security.User
import com.github.ikovalyov.model.security.UserRole
import com.github.ikovalyov.react.components.bootstrap.ScreenReaderSpan
import com.github.ikovalyov.react.components.bootstrap.nav.menuItem
import com.github.ikovalyov.react.components.template.CrudComponent
import kotlinx.datetime.Clock
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.nav
import react.dom.html.ReactHTML.ul
import web.cssom.ClassName

@OptIn(ExperimentalSerializationApi::class)
external interface IndexProps : Props {
    var currentUser: User?
    var userList: List<User>
    var templateList: List<Template>
}

@OptIn(ExperimentalSerializationApi::class)
val Index = FC<IndexProps> { props ->
    val currentUser = props.currentUser
    console.log(currentUser)

    if (currentUser != null) {
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
                CrudComponent {
                    decodeItem = {
                        Json.decodeFromString(Template.serializer(), it)
                    }
                    decodeItems = {
                        Json.decodeFromString(ListSerializer(Template.serializer()), it)
                    }
                    apiUri = Api.templateApiUrl
                    factory = {
                        Template(id = uuid4(), "", "")
                    }
                    header = "Template"
                }
                CrudComponent {
                    decodeItem = {
                        Json.decodeFromString(UserRole.serializer(), it)
                    }
                    decodeItems = {
                        Json.decodeFromString(ListSerializer(UserRole.serializer()), it)
                    }
                    apiUri = Api.userRoleApiUrl
                    factory = {
                        UserRole(uuid4(), Clock.System.now(), "")
                    }
                    header = "User Role"
                }
                CrudComponent {
                    decodeItem = {
                        Json.decodeFromString(User.serializer(), it)
                    }
                    decodeItems = {
                        Json.decodeFromString(ListSerializer(User.serializer()), it)
                    }
                    apiUri = Api.userApiUrl
                    factory = {
                        User(
                            uuid4(),
                            Email(ShortString("changeme@invalid.tld")),
                            false,
                            "",
                            emptyList(),
                            Password(ShortString(""))
                        )
                    }
                    header = "User"
                }
                CrudComponent {
                    decodeItem = {
                        Json.decodeFromString(Article.serializer(), it)
                    }
                    decodeItems = {
                        Json.decodeFromString(ListSerializer(Article.serializer()), it)
                    }
                    apiUri = Api.articleApiUrl
                    factory = {
                        Article(
                            id = uuid4(),
                            name = "",
                            abstract = "",
                            body = "",
                            author = currentUser,
                            tags = emptyList(),
                            meta = emptyList(),
                            template = null,
                            userList = props.userList,
                            templateList = props.templateList
                        )
                    }
                    header = "Article"
                }
            }
        }
    }
}
