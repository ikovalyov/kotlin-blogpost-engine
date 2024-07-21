package com.github.ikovalyov.react.components.template.table

import com.github.ikovalyov.model.markers.IEditable
import com.github.ikovalyov.model.markers.getFieldValueAsString
import com.github.ikovalyov.styles.Colors
import emotion.react.css
import js.objects.jso
import react.ChildrenBuilder
import react.FC
import react.Fragment
import react.PropsWithChildren
import react.create
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.table
import react.dom.html.ReactHTML.tbody
import react.dom.html.ReactHTML.td
import react.dom.html.ReactHTML.th
import react.dom.html.ReactHTML.thead
import react.dom.html.ReactHTML.tr
import tanstack.react.table.renderCell
import tanstack.react.table.renderHeader
import tanstack.react.table.useReactTable
import tanstack.table.core.ColumnDef
import tanstack.table.core.ColumnDefTemplate
import tanstack.table.core.StringOrTemplateHeader
import tanstack.table.core.Table
import tanstack.table.core.getCoreRowModel
import web.cssom.Auto
import web.cssom.BorderCollapse
import web.cssom.Cursor
import web.cssom.FontWeight
import web.cssom.LineStyle
import web.cssom.Margin
import web.cssom.Padding
import web.cssom.TextAlign
import web.cssom.WhiteSpace
import web.cssom.px

external interface TableProps<T : IEditable> : PropsWithChildren {
    var items: Array<T>?
    var onEditClick: (T) -> Unit
    var onDeleteClick: (T) -> Unit
    var onViewClick: (T) -> Unit
}

private fun <T : IEditable> ChildrenBuilder.table(props: TableProps<T>) {
    val items = props.items
    if (!items.isNullOrEmpty()) {
        val tableColumns = buildTableColumns(props, items.first())
        val table = useReactTable<T>(
            options = jso {
                this.data = items
                this.columns = tableColumns
                this.getCoreRowModel = getCoreRowModel()
            },
        )
        buildTableBody(table)
    }
}

private fun <T : IEditable> buildTableColumns(componentProps: TableProps<T>, item: T): Array<ColumnDef<T, *>> {
    val metadataList = item.getMetadata()

    val columns = metadataList.filterIsInstance<IEditable.EditableMetadata<*, T>>().mapIndexed { counter, metadata ->
        jso<ColumnDef<T, String>> {
            header = StringOrTemplateHeader(metadata.fieldType::class.simpleName!!)

            accessorFn = { row, _ ->
                val itemMetadata = row.getMetadata().filterIsInstance<IEditable.EditableMetadata<*, T>>()[counter]
                val str = getFieldValueAsString(itemMetadata) ?: ""
                if (str.length > 128) {
                    str.substring(0, 128)
                } else {
                    str
                }
            }
        }
    }.toMutableList()
    columns.add(
        jso {
            id = "Action"
            header = StringOrTemplateHeader("Action")
            accessorKey = "id"
            cell = ColumnDefTemplate { props ->
                Fragment.create {
                    div {
                        buttonChild<T> {
                            onClick = componentProps.onViewClick
                            body = props.row.original
                            text = "view"
                        }
                        buttonChild<T> {
                            onClick = componentProps.onEditClick
                            body = props.row.original
                            text = "update"
                        }
                        buttonChild<T> {
                            onClick = componentProps.onDeleteClick
                            body = props.row.original
                            text = "delete"
                        }
                    }
                }
            }
        },
    )
    return columns.toTypedArray()
}

private fun <T : IEditable> ChildrenBuilder.buildTableBody(table: Table<T>) {
    div {
        table {
            css {
                width = 400.px
                borderSpacing = 0.px
                borderCollapse = BorderCollapse.collapse
                whiteSpace = WhiteSpace.nowrap
                borderWidth = 2.px
                borderStyle = LineStyle.solid
                borderColor = Colors.Stroke.Gray
                margin = Margin(Auto.auto, Auto.auto)
            }

            thead {
                css {
                    color = Colors.Text.Gray
                    fontSize = 18.px
                    backgroundColor = Colors.Background.Gray
                }
                for (headerGroup in table.getHeaderGroups()) {
                    tr {
                        for (h in headerGroup.headers) {
                            th {
                                css {
                                    fontWeight = FontWeight.normal
                                    padding = Padding(4.px, 12.px)
                                    borderRightColor = Colors.Stroke.Gray
                                    borderRight = LineStyle.solid
                                    borderBottomColor = Colors.Stroke.Gray
                                    borderBottom = LineStyle.solid
                                    lastChild { borderRight = LineStyle.hidden }
                                }
                                +renderHeader(h)
                            }
                        }
                    }
                }
            }

            tbody {
                css {
                    color = Colors.Text.Black
                    backgroundColor = Colors.Background.White
                    textAlign = TextAlign.start
                }
                for (row in table.getRowModel().rows) {
                    tr {
                        css {
                            fontSize = 16.px
                            cursor = Cursor.pointer
                            borderBottomColor = Colors.Stroke.LightGray
                            borderBottom = LineStyle.solid
                            hover { backgroundColor = Colors.Background.Gray }
                        }
                        for (cell in row.getVisibleCells()) {
                            td {
                                css {
                                    padding = Padding(10.px, 12.px)
                                }
                                +renderCell(cell)
                            }
                        }
                    }
                }
            }
        }
    }
}

private val Table: FC<TableProps<*>> = FC { prop -> table(prop) }

fun <T : IEditable> ChildrenBuilder.tableChild(block: TableProps<T>.() -> Unit) {
    child(type = Table, props = jso(block))
}
