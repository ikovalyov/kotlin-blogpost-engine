package com.github.ikovalyov.react.components.template.table

import com.github.ikovalyov.extenstion.extraAttrs
import com.github.ikovalyov.model.markers.IEditable
import com.github.ikovalyov.styles.Colors
import kotlinext.js.jsObject
import kotlinx.css.BorderCollapse
import kotlinx.css.BorderStyle
import kotlinx.css.Cursor
import kotlinx.css.FontWeight
import kotlinx.css.LinearDimension
import kotlinx.css.TextAlign
import kotlinx.css.WhiteSpace
import kotlinx.css.backgroundColor
import kotlinx.css.borderBottom
import kotlinx.css.borderBottomColor
import kotlinx.css.borderCollapse
import kotlinx.css.borderColor
import kotlinx.css.borderRight
import kotlinx.css.borderRightColor
import kotlinx.css.borderSpacing
import kotlinx.css.borderStyle
import kotlinx.css.borderWidth
import kotlinx.css.color
import kotlinx.css.cursor
import kotlinx.css.fontSize
import kotlinx.css.fontWeight
import kotlinx.css.margin
import kotlinx.css.padding
import kotlinx.css.px
import kotlinx.css.textAlign
import kotlinx.css.whiteSpace
import kotlinx.css.width
import react.FC
import react.PropsWithChildren
import react.RBuilder
import react.buildElement
import react.dom.div
import react.dom.tr
import react.fc
import react.table.Column
import react.table.RenderType
import react.table.TableInstance
import react.table.columns
import react.table.useTable
import react.useMemo
import styled.css
import styled.styledDiv
import styled.styledTable
import styled.styledTbody
import styled.styledTd
import styled.styledTh
import styled.styledThead
import styled.styledTr

external interface TableProps<T : IEditable<T>> : PropsWithChildren {
    var items: Array<T>?
    var onEditClick: (T) -> Unit
    var onDeleteClick: (T) -> Unit
    var onViewClick: (T) -> Unit
}

private fun <T : IEditable<T>> RBuilder.Table(
    props: TableProps<T>,
) {
    val items = props.items
    if (!items.isNullOrEmpty()) {
        val tableColumns = buildTableColumns(props, items.first())
        val table = useTable<T>(
            options = jsObject {
                this.data = items
                this.columns = tableColumns
            }
        )
        buildTableBody(table)
    }
}

private fun <T : IEditable<T>> buildTableColumns(
    componentProps: TableProps<T>,
    item: T
): Array<out Column<T, *>> {
    return useMemo {
        columns {
            val metadataList = item.getMetadata()
            metadataList.forEachIndexed { counter, metadata ->
                column<String> {
                    header = metadata.fieldType::class.simpleName!!
                    accessorFunction = {
                        val itemMetadata = it.getMetadata()[counter]
                        val str = it.getFieldValueAsString(itemMetadata)
                        if (str.length > 128) {
                            str.substring(0, 128)
                        } else str
                    }
                }
            }
            column<T> {
                id = "Action"
                header = "Action"
                accessor = "id"
                cellFunction =
                    { props ->
                        buildElement {
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

private fun <T : IEditable<T>> RBuilder.buildTableBody(table: TableInstance<T>) {
    styledDiv {
        styledTable {
            extraAttrs = table.getTableProps()

            css {
                width = 400.px
                borderSpacing = 0.px
                borderCollapse = BorderCollapse.collapse
                whiteSpace = WhiteSpace.nowrap
                borderWidth = 2.px
                borderStyle = BorderStyle.solid
                borderColor = Colors.Stroke.Gray
                margin(LinearDimension.auto)
            }
            styledThead {
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

                            styledTh {
                                extraAttrs = header.getHeaderProps()
                                css {
                                    fontWeight = FontWeight.normal
                                    padding(4.px, 12.px)
                                    borderRightColor = Colors.Stroke.Gray
                                    borderRight = BorderStyle.solid.toString()
                                    if (header.columns != null) {
                                        borderBottomColor = Colors.Stroke.Gray
                                        borderBottom = BorderStyle.solid.toString()
                                    }
                                    lastChild { borderRight = "none" }
                                }
                                +header.render(RenderType.Header)
                            }
                        }
                    }
                }
            }
            styledTbody {
                extraAttrs = table.getTableBodyProps()

                css {
                    color = Colors.Text.Black
                    backgroundColor = Colors.Background.White
                    textAlign = TextAlign.start
                }
                for (row in table.rows) {
                    table.prepareRow(row)

                    styledTr {
                        extraAttrs = row.getRowProps()
                        css {
                            fontSize = 16.px
                            cursor = Cursor.pointer
                            borderBottomColor = Colors.Stroke.LightGray
                            borderBottom = BorderStyle.solid.toString()
                            hover { backgroundColor = Colors.Background.Gray }
                        }
                        for (cell in row.cells) {
                            styledTd {
                                extraAttrs = cell.getCellProps()
                                css { padding(10.px, 12.px) }
                                +cell.render(RenderType.Cell)
                            }
                        }
                    }
                }
            }
        }
    }
}

private val Table: FC<TableProps<*>> = fc { Table(it) }

fun <T : IEditable<T>> RBuilder.Table(block: TableProps<T>.() -> Unit) {
    child(type = Table, props = jsObject(block))
}
