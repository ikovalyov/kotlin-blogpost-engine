package com.github.ikovalyov.react.components.template

import com.github.ikovalyov.model.Template
import com.github.ikovalyov.react.components.template.table.Table
import kotlinext.js.jsObject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.html.ButtonType
import kotlinx.html.js.onClickFunction
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.child
import react.dom.attrs
import react.dom.button

external interface TemplateListProps : RProps {
    var switchToViewState: (Template) -> Unit
    var switchToEditState: (Template) -> Unit
    var switchToInsertState: () -> Unit
    var deleteItem: suspend (Template) -> Unit
    var templates: List<Template>
}

class TemplateList : RComponent<TemplateListProps, RState>() {
    override fun RBuilder.render() {
        child(
            component = Table,
            props =
                jsObject {
                    templates =
                        props
                            .templates
                            ?.map {
                                if (it.template.length > 255) {
                                    it.copy(template = it.template.substring(0, 125) + "...")
                                } else it
                            }
                            ?.toTypedArray()
                    onViewClick = { props.switchToViewState(it) }
                    onEditClick = { props.switchToEditState(it) }
                    onDeleteClick = { GlobalScope.async { props.deleteItem(it) } }
                })
        button {
            attrs {
                text("Add new")
                name = "new"
                type = ButtonType.button
                onClickFunction = { props.switchToInsertState() }
            }
        }
    }
}
