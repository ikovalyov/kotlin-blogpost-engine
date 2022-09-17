package com.github.ikovalyov.react.components.template.table

import com.github.ikovalyov.extenstion.extraAttrs
import com.github.ikovalyov.model.markers.IEditable
import com.github.ikovalyov.model.markers.getFieldValueAsString
import com.github.ikovalyov.styles.Colors
import csstype.Auto
import csstype.BorderCollapse
import csstype.Cursor
import csstype.FontWeight
import csstype.LineStyle
import csstype.Margin
import csstype.Padding
import csstype.TextAlign
import csstype.WhiteSpace
import csstype.px
import emotion.react.css
import kotlinx.js.jso
import react.ChildrenBuilder
import react.FC
import react.Fragment
import react.PropsWithChildren
import react.create
import react.dom.html.ReactHTML
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.table
import react.dom.html.ReactHTML.tbody
import react.dom.html.ReactHTML.td
import react.dom.html.ReactHTML.th
import react.dom.html.ReactHTML.thead
import react.dom.html.ReactHTML.tr
import react.table.Column
import react.table.RenderType
import react.table.TableInstance
import react.table.columns
import react.table.useTable
import react.useMemo

external interface TableProps<T : IEditable> : PropsWithChildren {
    var items: Array<T>?
    var onEditClick: (T) -> Unit
    var onDeleteClick: (T) -> Unit
    var onViewClick: (T) -> Unit
}

private fun <T : IEditable> ChildrenBuilder.Table(props: TableProps<T>) {
    val items = props.items
    if (!items.isNullOrEmpty()) {
        val tableColumns = buildTableColumns(props, items.first())
        val table = useTable<T>(
            options = jso {
                this.data = items
                this.columns = tableColumns
            }
        )
        buildTableBody(table)
    }
}

private fun <T : IEditable> buildTableColumns(componentProps: TableProps<T>, item: T): Array<out Column<T, *>> {
    return useMemo {
        columns {
            val metadataList = item.getMetadata()
            metadataList.filterIsInstance<IEditable.EditableMetadata<*, T>>().forEachIndexed { counter, metadata ->
                column {
                    header = metadata.fieldType::class.simpleName!!
                    accessorFunction = {
                        val itemMetadata = it.getMetadata().filterIsInstance<IEditable.EditableMetadata<*, T>>()[counter]
                        val str = it.getFieldValueAsString(itemMetadata)
                        if (str.length > 128) {
                            str.substring(0, 128)
                        } else {
                            str
                        }
                    }
                }
            }
            column<T> {
                id = "Action"
                header = "Action"
                accessor = "id"
                cellFunction =
                    { props ->
                        Fragment.create {
                            div {
                                Button<T> {
                                    onClick = componentProps.onViewClick
                                    body = props.row.original
                                    text = "view"
                                }
                                Button<T> {
                                    onClick = componentProps.onEditClick
                                    body = props.row.original
                                    text = "update"
                                }
                                Button<T> {
                                    onClick = componentProps.onDeleteClick
                                    body = props.row.original
                                    text = "delete"
                                }
                            }
                        }
                    }
            }
        }
    }
}

private fun <T : IEditable> ChildrenBuilder.buildTableBody(table: TableInstance<T>) {
    div {
        table {
            extraAttrs = table.getTableProps()

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
                for (headerGroup in table.headerGroups) {
                    tr {
                        extraAttrs = headerGroup.getHeaderGroupProps()
                        for (h in headerGroup.headers) {
                            val originalHeader = h.placeholderOf
                            val header = originalHeader ?: h

                            th {
                                extraAttrs = header.getHeaderProps()
                                css {
                                    fontWeight = FontWeight.normal
                                    padding = Padding(4.px, 12.px)
                                    borderRightColor = Colors.Stroke.Gray
                                    borderRight = LineStyle.solid
                                    if (header.columns != null) {
                                        borderBottomColor = Colors.Stroke.Gray
                                        borderBottom = LineStyle.solid
                                    }
                                    lastChild { borderRight = LineStyle.hidden }
                                }
                                +header.render(RenderType.Header)
                            }
                        }
                    }
                }
            }

            tbody {
                extraAttrs = table.getTableBodyProps()

                css {
                    color = Colors.Text.Black
                    backgroundColor = Colors.Background.White
                    textAlign = TextAlign.start
                }
                for (row in table.rows) {
                    table.prepareRow(row)

                    tr {
                        extraAttrs = row.getRowProps()
                        css {
                            fontSize = 16.px
                            cursor = Cursor.pointer
                            borderBottomColor = Colors.Stroke.LightGray
                            borderBottom = LineStyle.solid
                            hover { backgroundColor = Colors.Background.Gray }
                        }
                        for (cell in row.cells) {
                            td {
                                extraAttrs = cell.getCellProps()
                                css {
                                    padding = Padding(10.px, 12.px)
                                }
                                +cell.render(RenderType.Cell)
                            }
                        }
                    }
                }
            }
        }
    }
}

private val Table: FC<TableProps<*>> = FC { Table(it) }

fun <T : IEditable> ChildrenBuilder.Table(block: TableProps<T>.() -> Unit) {
    child(type = Table, props = jso(block))
}
