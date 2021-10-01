package com.github.ikovalyov

import com.github.ikovalyov.model.Template
import com.github.ikovalyov.react.components.template.CrudComponent
import kotlinx.browser.document
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import react.dom.*

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
    }
  }
}
