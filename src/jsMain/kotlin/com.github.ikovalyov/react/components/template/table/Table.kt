package com.github.ikovalyov.react.components.template.table

import com.github.ikovalyov.model.Template
import com.github.ikovalyov.styles.Colors
import kotlinext.js.Object
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
import react.PropsWithChildren
import react.buildElement
import react.dom.div
import react.dom.setProp
import react.dom.tr
import react.fc
import react.table.RenderType
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

external interface TemplateProps : PropsWithChildren {
  var templates: Array<Template>?
  var onEditClick: (Template) -> Unit
  var onDeleteClick: (Template) -> Unit
  var onViewClick: (Template) -> Unit
}

val Table =
    fc<TemplateProps> { componentProps ->
      val tableColumns = useMemo {
        columns<Template> {
          column<String> {
            header = "Id"
            accessorFunction = { it.id.toString() }
          }
          column<String> {
            header = "Template"
            accessorFunction = { it.body }
          }
          column<String> {
            header = "Action"
            accessor = "id"
            cellFunction =
                { props ->
                  buildElement {
                    div {
                      child(Button::class) {
                        attrs {
                          onClick = componentProps.onViewClick
                          template = props.row.original
                          text = "view"
                        }
                      }
                      child(Button::class) {
                        attrs {
                          onClick = componentProps.onEditClick
                          template = props.row.original
                          text = "update"
                        }
                      }
                      child(Button::class) {
                        attrs {
                          onClick = componentProps.onDeleteClick
                          template = props.row.original
                          text = "delete"
                        }
                      }
                    }
                  }
                }
          }
        }
      }

      val table =
          useTable<Template>(
              options =
                  jsObject {
                    this.data = componentProps.templates ?: emptyArray()
                    this.columns = tableColumns
                  })

      if (!componentProps.templates.isNullOrEmpty()) {
        styledDiv {
          styledTable {
            for (key in Object.keys(table.getTableProps())) {
              setProp(key, table.getTableProps().asDynamic()[key])
            }

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
                  for (key in Object.keys(headerGroup.getHeaderGroupProps())) {
                    setProp(key, headerGroup.getHeaderGroupProps().asDynamic()[key])
                  }

                  for (h in headerGroup.headers) {
                    val originalHeader = h.placeholderOf
                    val header = originalHeader ?: h

                    styledTh {
                      for (key in Object.keys(header.getHeaderProps())) {
                        setProp(key, header.getHeaderProps().asDynamic()[key])
                      }

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
              for (key in Object.keys(table.getTableBodyProps())) {
                setProp(key, table.getTableBodyProps().asDynamic()[key])
              }

              css {
                color = Colors.Text.Black
                backgroundColor = Colors.Background.White
                textAlign = TextAlign.start
              }
              for (row in table.rows) {
                table.prepareRow(row)

                styledTr {
                  for (key in Object.keys(row.getRowProps())) {
                    setProp(key, row.getRowProps().asDynamic()[key])
                  }

                  css {
                    fontSize = 16.px
                    cursor = Cursor.pointer
                    borderBottomColor = Colors.Stroke.LightGray
                    borderBottom = BorderStyle.solid.toString()
                    hover { backgroundColor = Colors.Background.Gray }
                  }
                  for (cell in row.cells) {
                    styledTd {
                      for (key in Object.keys(cell.getCellProps())) {
                        setProp(key, cell.getCellProps().asDynamic()[key])
                      }

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
    }
