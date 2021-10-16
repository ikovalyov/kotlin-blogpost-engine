package com.github.ikovalyov

import com.benasher44.uuid.uuid4
import com.github.ikovalyov.model.Template
import com.github.ikovalyov.model.security.Email
import com.github.ikovalyov.model.security.Password
import com.github.ikovalyov.model.security.ShortString
import com.github.ikovalyov.model.security.User
import com.github.ikovalyov.model.security.UserRole
import com.github.ikovalyov.react.components.template.CrudComponent
import kotlinx.browser.document
import kotlinx.datetime.Clock
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import react.dom.render

suspend fun main() {
    render(document.getElementById("root")) {
        child(App::class) {}
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
